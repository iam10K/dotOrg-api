package com.dotorg.api.endpoints.v1;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Membership;
import com.dotorg.api.objects.News;
import com.dotorg.api.objects.User;
import com.dotorg.api.utils.ValidationHelper;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

@ApiReference(BaseEndpointV1.class)
public class NewsEndpointV1 {

    private static final Logger logger = Logger.getLogger(NewsEndpointV1.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Inserts a new {@code News}.
     */
    @ApiMethod(
            name = "groups.events.create",
            path = "groups/{groupId}/news",
            httpMethod = ApiMethod.HttpMethod.POST)
    public News create(News news, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException, InvalidParameterException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null) {
            throw new NotFoundException("Could not find Membership for group ID-" + groupId + " and user ID-" + user.getUserId());
        }

        Member member = ofy().load().key(membership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + membership.getMemberId());
        }
        //  FUTURE Permission

        ValidationHelper.validateNewNews(news);

        return createNews(news, groupId, user, member);
    }

    /**
     * Updates an existing {@code News}.
     *
     * @param newsId  the ID of the entity to be updated
     * @param newNews the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code newsId} does not correspond to an existing
     *                           {@code News}
     */
    @ApiMethod(
            name = "groups.events.update",
            path = "groups/{groupId}/news/{newsId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public News update(News newNews, @Named("newsId") Long newsId, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException, InvalidParameterException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null) {
            throw new NotFoundException("Could not find Membership for group ID-" + groupId + " and user ID-" + user.getUserId());
        }

        Member member = ofy().load().key(membership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + membership.getMemberId());
        }

        News news = ofy().load().type(News.class).parent(Key.create(Group.class, groupId)).id(newsId).now();
        if (news == null) {
            throw new NotFoundException("Could not find news with ID: " + newsId);
        }

        ValidationHelper.validateNewsOfGroup(news, groupId);

        // FUTURE Permission to update
        // If news creator allow update
        ValidationHelper.validateCreatorOfNews(news, member);

        ValidationHelper.validateNewNews(newNews);

        return updateNews(news, newNews);
    }

    /**
     * Deletes the specified {@code News}.
     *
     * @param newsId the ID of the entity to delete
     * @throws NotFoundException if the {@code newsId} does not correspond to an existing
     *                           {@code News}
     */
    @ApiMethod(
            name = "groups.events.delete",
            path = "groups/{groupId}/news/{newsId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("newsId") Long newsId, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null) {
            throw new NotFoundException("Could not find Membership for group ID-" + groupId + " and user ID-" + user.getUserId());
        }

        Member member = ofy().load().key(membership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + membership.getMemberId());
        }

        News news = ofy().load().type(News.class).parent(Key.create(Group.class, groupId)).id(newsId).now();
        if (news == null) {
            throw new NotFoundException("Could not find news with ID: " + newsId);
        }

        ValidationHelper.validateNewsOfGroup(news, groupId);

        // FUTURE Permission to delete
        // If news creator allow delete
        ValidationHelper.validateCreatorOfNews(news, member);

        ofy().delete().entity(news).now();
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "groups.events.list",
            path = "groups/{groupId}/news",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<News> list(@Named("groupId") Long groupId, @Named("token") String token, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find group with ID: " + groupId);
        }

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<News> query = ofy().load().type(News.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<News> queryIterator = query.iterator();
        List<News> newsList = new ArrayList<News>(limit);
        while (queryIterator.hasNext()) {
            newsList.add(queryIterator.next());
        }
        return CollectionResponse.<News>builder().setItems(newsList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private News createNews(News news, Long groupId, User user, Member member) {
        news.createNewNews(member, user, Key.create(Group.class, groupId));

        Key<News> newsKey = ofy().save().entity(news).now();

        news = ofy().load().key(newsKey).now();
        logger.info("Created Event with ID: " + news.getNewsId());
        return news;
    }

    private News updateNews(News news, News newNews) {
        news.setTitle(newNews.getTitle());
        news.setDescription(newNews.getDescription());

        ofy().save().entity(news).now();
        return news;
    }
}