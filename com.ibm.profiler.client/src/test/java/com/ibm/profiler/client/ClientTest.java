package com.ibm.profiler.client;

/*
 *-----------------------------------------------------------------
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WebSphere Commerce
 *
 * (C) Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *-----------------------------------------------------------------
 */

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.logger.PerformanceLogger;
import com.ibm.logger.jmx.PerformanceLoggerManagerMXBean;
import com.ibm.profiler.client.jmx.JMXStatistic;
import com.ibm.profiler.client.jmx.TotalLogEntry;

/**
 * @author Bryan Johnson
 * 
 */
public class ClientTest {

    public static final Logger LOGGER = Logger.getLogger( "com.ibm.service.entry.test.MyEntryLogger" );

    private static MBeanServerConnection jmxConn = null;

    private static PerformanceLoggerManagerMXBean jmxBean = null;

    @Test
    public void basicJMXConnectionTest() {
        long start = System.currentTimeMillis();

        MBeanServerConnection conn = getMBeanServerConnection();
        Assert.assertNotNull( conn );
        System.out.println( "basicJMXConnectionTest total Time: " + ( System.currentTimeMillis() - start ) );

    }

    private MBeanServerConnection getMBeanServerConnection() {
        if ( jmxConn == null )
            jmxConn = PerformanceLoggerManagerCLI.getMBeanServerConnection( null, null );
        return jmxConn;
    }

    @Test
    public void basicJMXBeanTest() {
        long start = System.currentTimeMillis();
        initialBean();
        PerformanceLoggerManagerMXBean myBean = getPerformanceLoggerManagerMBean( getMBeanServerConnection() );
        Assert.assertNotNull( myBean );
        System.out.println( "basicJMXBeanTest total Time: " + ( System.currentTimeMillis() - start ) );

    }

    private void initialBean() {
        PerformanceLogger.setEnabled( true );
        PerformanceLogger.startLogging( "doNothing" );
        PerformanceLogger.stopLogging( "doNothing" );
        PerformanceLogger.clear();

    }

    private PerformanceLoggerManagerMXBean getPerformanceLoggerManagerMBean( MBeanServerConnection mBeanServerConnection ) {
        if ( jmxBean == null )
            jmxBean = PerformanceLoggerManagerCLI.getPerformanceLoggerManagerMBean( getMBeanServerConnection() );
        return jmxBean;
    }

    @Test
    public void basicPerformanceTest() {
        long start = System.currentTimeMillis();
        initialBean();

        PerformanceLogger.startLogging( "doNothing" );
        PerformanceLogger.stopLogging( "doNothing" );
        PerformanceLogger.startLogging( "doNothing2" );
        PerformanceLogger.stopLogging( "doNothing2" );
        JMXParser parser = new JMXParser();
        try {
            HashMap<String, Boolean> dspProps = new HashMap<String, Boolean>();
            dspProps.put( JMXParser.SUPPRESS_OUTPUT, false );
            dspProps.put( JMXParser.MESSAGE_FABRIC, false );
            dspProps.put( JMXParser.PERFORMANCE_LOGGER, false );
            dspProps.put( JMXParser.ALL_STATS, true );

            List<JMXStatistic> myStats = parser.parse( getMBeanServerConnection(), getMBeanServerConnection().queryMBeans( null, null ), dspProps );
            TotalLogEntry myStat = extractStat( myStats, "doNothing" );
            System.out.println( "Found stat: " + myStat );
            Assert.assertNotNull( myStat );

            Assert.assertEquals( "1", myStat.getCalls() );
            Assert.assertEquals( "total", myStat.getType() );
        } catch ( Exception e ) {
            System.out.println( "Parser failed; " + e.getMessage() );
            e.printStackTrace();
            Assert.fail();
        }
        System.out.println( "basicPerformanceTest total Time: " + ( System.currentTimeMillis() - start ) );
    }

    private TotalLogEntry extractStat( List<JMXStatistic> myStats, String string ) {
        for ( JMXStatistic stat : myStats ) {
            if ( stat instanceof TotalLogEntry ) {
                TotalLogEntry myStat = (TotalLogEntry) stat;
                if ( string != null && myStat.getId().equals( string ) )
                    return myStat;
            }
            // System.out.println( "Testing: " + stat );
        }
        return null;
    }

}
