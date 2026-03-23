package io.aotchoun.blog.dto.response;

public class UserStatsResponse {
    private Long userId;
    private String username;
    private long postCount;
    private long followerCount;
    private long followingCount;
    private boolean followedByCurrentUser;

    public UserStatsResponse() {}

    public UserStatsResponse(Long userId, String username, long postCount, 
                            long followerCount, long followingCount, 
                            boolean followedByCurrentUser) {
        this.userId = userId;
        this.username = username;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.followedByCurrentUser = followedByCurrentUser;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public long getPostCount() { return postCount; }
    public long getFollowerCount() { return followerCount; }
    public long getFollowingCount() { return followingCount; }
    public boolean isFollowedByCurrentUser() { return followedByCurrentUser; }
    
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPostCount(long postCount) { this.postCount = postCount; }
    public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
    public void setFollowingCount(long followingCount) { this.followingCount = followingCount; }
    public void setFollowedByCurrentUser(boolean followedByCurrentUser) { this.followedByCurrentUser = followedByCurrentUser; }
}