/**
 * @projectName javaagent
 * @package com.wenshuo.agent.applog
 * @className com.wenshuo.agent.applog.ConsoleAppLog
 * @copyright Copyright 2023 Thunisoft, Inc All rights reserved.
 */

package com.wenshuo.agent.applog;

/**
 * ConsoleAppLog
 *
 * @author dingjsh
 * @description
 * @date 2023-10-16 17:37
 * @since 2.1.3
 */
class ConsoleAppLog implements IAppLog {

    @Override
    public void info(String message) {
        System.out.println("[info ] " + message);
    }

    @Override
    public void warn(String message) {
        System.out.println("[warn ] " + message);
    }

    @Override
    public void error(String message) {
        System.err.println("[error ] " + message);
    }

    @Override
    public void error(String message, Throwable t) {
        System.err.println("[error ] " + message);
    }

}