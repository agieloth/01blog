package io.aotchoun.blog.dto.response;

public class FollowResponse {
    private boolean following;
    private long followerCount;

    public FollowResponse() {}

    public FollowResponse(boolean following, long followerCount) {
        this.following = following;
        this.followerCount = followerCount;
    }

    public boolean isFollowing() { return following; }
    public long getFollowerCount() { return followerCount; }
    public void setFollowing(boolean following) { this.following = following; }
    public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
}