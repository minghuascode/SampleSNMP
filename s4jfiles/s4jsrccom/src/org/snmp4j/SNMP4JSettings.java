/*_############################################################################
  _## 
  _##  SNMP4J 2 - SNMP4JSettings.java  
  _## 
  _##  Copyright (C) 2003-2013  Frank Fock and Jochen Katz (SNMP4J.org)
  _##  
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##  
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##  
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##  
  _##########################################################################*/
package org.snmp4j;

import org.snmp4j.util.*;

/**
 * The <code>SNMP4JSettings</code> class implements a central configuration
 * class of the SNMP4J framework. As a rule of thumb, changes to the default
 * configuration should be made before any other classes of the SNMP4J API are
 * instantiated or referenced by the application code.
 *
 * @author Frank Fock
 * @version 2.2
 * @since 1.5
 */
public final class SNMP4JSettings {

  public enum ReportSecurityLevelStrategy {
    /**
     * This is the SNMP4J < 2.2.0 default strategy. If the report receiver would not
     * be able to process the REPORT, a lesser security level is used.
     */
    noAuthNoPrivIfNeeded,
    /**
     * The standard report strategy is conforming to RFC 3412 and 3414. Security level noAuthNoPriv is allowed
     * for engine ID discovery and wrong username reports.
     */
    standard,
    /**
     * Never use noAuthNoPriv security level for reports.
     */
    neverNoAuthNoPriv };

  /**
   * Specifies whether SNMP4J can be extended by own implementation of
   * security protocols, transport mappings, address types, SMI syntaxes, etc.
   * through property files defined via System properties.
   * If set to <code>false</code> all classes SNMP4J is aware of will be
   * used hard coded which speeds up initialization and is required to use
   * SNMP4J in a secure environment where System properties are not available
   * (i.e. in an unsigned applet).
   * @since 1.2.2
   */
  private static boolean extensibilityEnabled = false;

  /**
   * By default SNMP4J (and SNMP4J-Agent*) catch runtime exceptions at thread
   * boundaries of API controlled threads. In SNMP4J such a thread runs in each
   * {@link TransportMapping} and in each {@link Snmp} session object. To ensure
   * robust runtime behavior, unexpected runtime exceptions are caught and
   * logged. If you need to localize and debug such exceptions then set this
   * value to <code>true</code>.
   *
   * @since 1.8.1
   */
  private static volatile boolean forwardRuntimeExceptions = false;

  /**
   * By default SNMP4J uses {@link Thread} instances to run
   * concurrent tasks. For environments with restricted thread management
   * like Java EE application servers, a custom thread factory can be used.
   *
   * @since 1.9
   */
  private static ThreadFactory threadFactory = new DefaultThreadFactory();

  /**
   * By default SNMP4J uses {@link java.util.Timer} instances to run
   * timed tasks. For environments with restricted thread management
   * like Java EE application servers, a custom timer factory can be used.
   *
   * @since 1.9
   */
  private static TimerFactory timerFactory = new DefaultTimerFactory();

  /**
   * By default SNMP4J uses the {@link SimpleOIDTextFormat} to convert
   * {@link org.snmp4j.smi.OID}s to/from a textual representation.
   *
   * @since 1.10
   */
  private static OIDTextFormat oidTextFormat = new SimpleOIDTextFormat();

  /**
   * By default SNMP4J uses the {@link SimpleVariableTextFormat} to convert
   * {@link org.snmp4j.smi.VariableBinding}s to/from a textual representation.
   *
   * @since 1.10
   */
  private static VariableTextFormat variableTextFormat =
      new SimpleVariableTextFormat();

  /**
   * The default Thread join timeout, used for example by the
   * {@link DefaultThreadFactory}, defines the maximum time to wait for a
   * Thread running a worker task to end that task (end join the main thread
   * if that Thread has been exclusively used for that task). The default value
   * is 60 seconds (1 min.).
   */
  private static long threadJoinTimeout = 60000;

  /**
   * This setting can used to be compatible with some faulty SNMP implementations
   * which send Counter64 objects with SNMPv1 PDUs.
   */
  private static boolean allowSNMPv2InV1 = false;

  /**
   * This flag enables/disables (default) whether REPORT PDUs should be send by
   * a command responder with securityLevel {@link org.snmp4j.security.SecurityLevel#noAuthNoPriv}
   * if otherwise the command generator would not able to receive the {@link PDU#REPORT}.
   * RFC 3412 §7.6.2.3 requires that the securityLevel is the same as in the confirmed class
   * PDU if not explicitly otherwise specified.
   * <p/>
   * For {@link org.snmp4j.mp.SnmpConstants#usmStatsUnsupportedSecLevels} reports, this would
   * always render the command responder unable to return the report.
   * <p/>
   * Setting this flag to {@link ReportSecurityLevelStrategy#noAuthNoPrivIfNeeded}
   * reactivates the behavior of SNMP4J prior to v2.2 which sends out the reports with
   * {@link org.snmp4j.security.SecurityLevel#noAuthNoPriv} if otherwise, the report would
   * not be sent out.
   * @since 2.2
   */
  private static ReportSecurityLevelStrategy reportSecurityLevelStrategy = ReportSecurityLevelStrategy.standard;

  /**
   * Enables (or disables) the extensibility feature of SNMP4J. When enabled,
   * SNMP4J checks certain properties files that describe which transport
   * mappings, address types, SMI syntaxes, security protocols, etc. should be
   * supported by SNMP4J.
   * <p>
   * By default, the extensibility feature is disabled which provides a faster
   * startup and since no system properties are read, it ensures that SNMP4J
   * can be used also in secure environments like applets.
   * @param enable
   *    if <code>true</code> activates extensibility or if <code>false</code>
   *    disables it. In the latter case, SNMP4J's default configuration will
   *    be used with all available features.
   * @since 1.2.2
   */
  public static void setExtensibilityEnabled(boolean enable) {
    extensibilityEnabled = enable;
  }

  /**
   * Tests if the extensibility feature is enabled.
   * @return
   *    if <code>true</code> the extensibility is enabled otherwise it is
   *    disabled. In the latter case, SNMP4J's default configuration will
   *    be used with all available features.
   * @since 1.2.2
   */
  public final static boolean isExtensibilityEnabled() {
    return extensibilityEnabled;
  }

  /**
   * Enables or disables runtime exception forwarding.
   * @see #forwardRuntimeExceptions
   * @param forwardExceptions
   *    <code>true</code> runtime exceptions are thrown on thread boundaries
   *    controlled by SNMP4J and related APIs. Default is <code>false</code>.
   * @since 1.8.1
   */
  public static void setForwardRuntimeExceptions(boolean forwardExceptions) {
    forwardRuntimeExceptions = forwardExceptions;
  }

  /**
   * Indicates whether runtime exceptions should be thrown on thread boundaries
   * controlled by SNMP4J and related APIs.
   * @return
   *    <code>true</code> runtime exceptions are thrown on thread boundaries
   *    controlled by SNMP4J and related APIs. Default is <code>false</code>.
   * @since 1.8.1
   */
  public final static boolean isForwardRuntimeExceptions() {
    return forwardRuntimeExceptions;
  }

  /**
   * Gets the thread factory.
   * @return
   *    a ThreadFactory.
   * @since 1.9
   */
  public final static ThreadFactory getThreadFactory() {
    return threadFactory;
  }

  /**
   * Sets the thread factory for creating new threads of execution.
   * @param newThreadFactory
   *    a ThreadFactory (must not be <code>null</code>).
   * @since 1.9
   */
  public final static void setThreadFactory(ThreadFactory newThreadFactory) {
    if (newThreadFactory == null) {
      throw new NullPointerException();
    }
    threadFactory = newThreadFactory;
  }

  /**
   * Gets the timer factory.
   * @return
   *    a TimerFactory.
   * @since 1.9
   */
  public final static TimerFactory getTimerFactory() {
    return timerFactory;
  }

  /**
   * Sets the timer factory for creating new timer instances.
   * @param newTimerFactory
   *    a TimerFactory (must not be <code>null</code>).
   * @since 1.9
   */
  public final static void setTimerFactory(TimerFactory newTimerFactory) {
    if (newTimerFactory == null) {
      throw new NullPointerException();
    }
    timerFactory = newTimerFactory;
  }

  /**
   * Gets the OID text format for textual representation of OIDs.
   * @return
   *    an <code>OIDTextFormat</code> instance.
   * @since 1.10
   */
  public final static OIDTextFormat getOIDTextFormat() {
    return oidTextFormat;
  }

  /**
   * Sets the OID text format to be used by SNMP4J.
   * @param newOidTextFormat
   *    the new <code>OIDTextFormat</code> (must not be <code>null</code>).
   * @since 1.10
   */
  public final static void setOIDTextFormat(OIDTextFormat newOidTextFormat) {
    if (newOidTextFormat == null) {
      throw new NullPointerException();
    }
    oidTextFormat = newOidTextFormat;
  }

  /**
   * Gets the variable text format for textual representation of variable
   * bindings.
   * @return
   *    an <code>VariableTextFormat</code> instance.
   * @since 1.10
   */
  public final static VariableTextFormat getVariableTextFormat() {
    return variableTextFormat;
  }

  /**
   * Sets the variable text format to be used by SNMP4J.
   * @param newVariableTextFormat
   *    the new <code>VariableTextFormat</code> (must not be <code>null</code>).
   * @since 1.10
   */
  public final static void setVariableTextFormat(VariableTextFormat
                                                 newVariableTextFormat) {
    if (newVariableTextFormat == null) {
      throw new NullPointerException();
    }
    variableTextFormat = newVariableTextFormat;
  }

  /**
   * Gets the Thread join timeout used to join threads if no explicit timeout
   * is set.
   * @return
   *    the timeout millis.
   * @since 1.10.2
   */
  public static long getThreadJoinTimeout() {
    return threadJoinTimeout;
  }

  /**
   * Sets the Thread join timeout used to join threads if no explicit timeout
   * is set.
   * @param millis
   *    the maximum time in milli-seconds to wait for a Thread to join if no
   *    explicit timeout has been set.
   * @since 1.10.2
   */
  public static void setThreadJoinTimeout(long millis) {
    threadJoinTimeout = millis;
  }

  public static boolean isAllowSNMPv2InV1() {
    return allowSNMPv2InV1;
  }

  /**
   * Sets the compatibility flag for Counter64 usage in SNMPv1 PDUs and
   * the ability to decode SNMPv2 traps in SNMPv1 version PDUs.
   * The default is <code>false</code>.
   * Note: By setting this to <code>true</code> you disable SNMP standard
   * conformity.
   *
   * @param allowSNMPv2InV1
   *    if set to <code>true</code>, SNMP4J will allow Counter64 objects
   *    in SNMPv1 PDUs although the SNMPv1 standard does not allow this.
   *    There are several buggy implementations which send such v1 PDUs.
   *
   * @since 2.2
   */
  public static void setAllowSNMPv2InV1(boolean allowSNMPv2InV1) {
    SNMP4JSettings.allowSNMPv2InV1 = allowSNMPv2InV1;
  }

  public static ReportSecurityLevelStrategy getReportSecurityLevelStrategy() {
    return reportSecurityLevelStrategy;
  }

  public static void setReportSecurityLevelStrategy(ReportSecurityLevelStrategy reportSecurityLevelStrategy) {
    SNMP4JSettings.reportSecurityLevelStrategy = reportSecurityLevelStrategy;
  }
}
