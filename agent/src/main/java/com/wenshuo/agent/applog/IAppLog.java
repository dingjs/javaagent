/**
 * @projectName javaagent
 * @package com.wenshuo.agent.applog
 * @className com.wenshuo.agent.applog.IAppLog
 * @copyright Copyright 2023 Thunisoft, Inc All rights reserved.
 */

package com.wenshuo.agent.applog;

/**
 * IAppLog
 *
 * @author dingjsh
 * @description javaagent日志接口
 * @date 2023-10-16 11:46
 * @since 2.1.3
 */
public interface IAppLog {

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable t);

}