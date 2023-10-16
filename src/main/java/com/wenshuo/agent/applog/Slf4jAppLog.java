/**
 * @projectName javaagent
 * @package com.wenshuo.agent.applog
 * @className com.wenshuo.agent.applog.Slf4jAppLog
 * @copyright Copyright 2023 Thunisoft, Inc All rights reserved.
 */

package com.wenshuo.agent.applog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Slf4jAppLog
 *
 * @author dingjsh
 * @description
 * @date 2023-10-16 16:58
 * @since 2.1.3
 */
class Slf4jAppLog implements IAppLog {

    private Logger log;

    public Slf4jAppLog(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
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