package com.dotorg.api.wrappers;

import com.dotorg.api.objects.Member;

import java.util.List;

/**
 * dotOrg-api
 * Date Created: 6/27/2016
 * |
 * Original Package: com.dotorg.api.wrappers
 * |
 * COPYRIGHT 2016
 */
public class MembersWrapper {
    private List<Member> memberList;

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }
}
