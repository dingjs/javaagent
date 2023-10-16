/**
 * @projectName javaagent
 * @package com.wenshuo.agent.applog
 * @className com.wenshuo.agent.applog.CommonsLoggingAppLog
 * @copyright Copyright 2023 Thunisoft, Inc All rights reserved.
 */

package com.wenshuo.agent.applog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CommonsLoggingAppLog
 *
 * @author dingjsh
 * @description
 * @date 2023-10-16 17:35
 * @since 2.1.3
 */
class CommonsLoggingAppLog implements IAppLog {

    private Log log;

    public CommonsLoggingAppLog(Class<?> clazz) {
        log = LogFactory.getLog(clazz);
    }

    @Override
    public void info(String message) {
        log.info(message);

    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void error(String message, Throwable t) {
        log.error(message, t);
    }

}