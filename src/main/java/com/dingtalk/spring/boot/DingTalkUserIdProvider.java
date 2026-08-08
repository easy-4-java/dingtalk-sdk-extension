package com.dingtalk.spring.boot;

import org.apache.commons.lang3.StringUtils;

/**
 * Strategy interface for mapping between DingTalk user IDs and
 * application-specific user identifiers.
 * <p>Default implementations simply pass through the provided values.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public interface DingTalkUserIdProvider {

    /**
     * Maps a DingTalk user account to an application-specific user ID.
     * <p>The default implementation returns the account as-is.</p>
     *
     * @param corpId   the corporate ID
     * @param appId    the application ID
     * @param account  the DingTalk user account
     * @return the application-specific user ID
     */
    default String getUserIdByDingTalkUser(String corpId, String appId, String account)  {
        return account;
    }

    /**
     * Maps application-specific user IDs to a DingTalk user identifier.
     * <p>The default implementation joins the user IDs with commas.</p>
     *
     * @param corpId   the corporate ID
     * @param appId    the application ID
     * @param userIds  the application-specific user IDs
     * @return a comma-separated DingTalk user identifier
     */
    default String getDingTalkUserByUserId(String corpId, String appId, String... userIds) {
        return StringUtils.join(userIds, ",");
    }

}
