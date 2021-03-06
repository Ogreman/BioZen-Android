/*****************************************************************
BioZen

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/
package com.t2.compassionUtils;

import com.t2.biofeedback.device.shimmer.ShimmerDevice;



public class Util {
	
	public static String connectionStatusToString(int conectionStatus) {
		String statusString = "";
		switch (conectionStatus) {
			case BioSensor.CONN_CONNECTED:
				statusString = "Connected";
				break;
				
			case BioSensor.CONN_CONNECTING:
				statusString = "Connecting";
				break;
				
			case BioSensor.CONN_ERROR:
				statusString = "Error";
				break;
				
			case BioSensor.CONN_IDLE:
				statusString = "Idle";
				break;
				
			case BioSensor.CONN_PAIRED:
				statusString = "Paired but not connected";
				break;
			
			default:
				statusString = "";
				break;
			
		}	
		return statusString;
	}
	
	
	
	/**
	 * Converts a nicely formatted Bluetooth address to string of bytes representing the address
	 * 
	 * @param btAddress   ASCII version of address
	 * @return				Bytes version of address or null of error
	 */
	public static byte[] AsciiBTAddressToBytes(String btAddress) {
		// Make sure the address is of the form xx:xx:xx:xx:xx:xx
		// If not return null
		if (btAddress.length() != 17)
			return null;
		String[] tokens = btAddress.split(":");
		if (tokens.length != 6)
			return null;
		
		byte[] finalResult = new byte[tokens.length];
		int i = 0;
		for(String val: tokens) {
			finalResult[i++] =  Integer.decode("0x" + val).byteValue();
		}		
		
		return finalResult;
		
	}	

    /**
     * Returns resistance of Shimmer device based on adc value and range
     * @param gsrAdcVal				Value of adc
     * @param reportedGsrRange				Range of Shimmer device (As reported from the shimmer device)
     * @param configuredGsrRange	Configured range of Shimmer device (Set by the user)
     * @return						resistance
     */
    public static int GsrResistance(int gsrAdcVal, int reportedGsrRange, int configuredGsrRange)	{
        int resistance = Integer.MAX_VALUE;
        int range = configuredGsrRange;
        	
        	
        if (configuredGsrRange == ShimmerDevice.GSR_RANGE_AUTORANGE) {
        	// if the configured range is AUTORANGE then we'll use the actual reported range to switch on below
        	// instead of the configured range
        	range = reportedGsrRange;
        }
        
        switch (range) {
            // curve fitting using a 4th order polynomial
            case (int)ShimmerDevice.GSR_RANGE_HW_RES_40K:

            	// Check if invalid values, if so leave default resistance
            	// But only if NOT autorange
            	if (configuredGsrRange != ShimmerDevice.GSR_RANGE_AUTORANGE) {
                	if (gsrAdcVal < 1140 || gsrAdcVal > 3400 )
                		break;
            	}
        		
                resistance = (int)(
                    ((0.0000000065995) * Math.pow(gsrAdcVal, 4)) +
                    ((-0.000068950)    * Math.pow(gsrAdcVal, 3)) +
                    ((0.2699)          * Math.pow(gsrAdcVal, 2)) +
                    ((-476.9835)       * Math.pow(gsrAdcVal, 1)) + 340351.3341);
                break;
            case (int)ShimmerDevice.GSR_RANGE_HW_RES_287K:

            	// Check if invalid values, if so leave default resistance
            	// But only if NOT autorange
            	if (configuredGsrRange != ShimmerDevice.GSR_RANGE_AUTORANGE) {
	            	// Check if invalid values, if so leave default resistance
	            	if (gsrAdcVal < 1490 || gsrAdcVal > 3800 )
	            		break;
            	}
	            	
            	resistance = (int)(
                    ((0.000000013569627) * Math.pow(gsrAdcVal, 4)) +
                    ((-0.0001650399)     * Math.pow(gsrAdcVal, 3)) +
                    ((0.7541990)         * Math.pow(gsrAdcVal, 2)) +
                    ((-1572.6287856)     * Math.pow(gsrAdcVal, 1)) + 1367507.9270);
                break;

            case (int)ShimmerDevice.GSR_RANGE_HW_RES_1M:
            	// Check if invalid values, if so leave default resistance
            	// But only if NOT autorange
            	if (configuredGsrRange != ShimmerDevice.GSR_RANGE_AUTORANGE) {
            		if (gsrAdcVal < 1630 || gsrAdcVal > 3700 )
            			break;
            	}

                resistance = (int)(
                    ((0.00000002550036498) * Math.pow(gsrAdcVal, 4)) +
                    ((-0.00033136)         * Math.pow(gsrAdcVal, 3)) +
                    ((1.6509426597)        * Math.pow(gsrAdcVal, 2)) +
                    ((-3833.348044)        * Math.pow(gsrAdcVal, 1)) + 3806317.6947);
                break;
            case (int)ShimmerDevice.GSR_RANGE_HW_RES_3M3:
            	// Check if invalid values, if so leave default resistance
            	// But only if NOT autorange
            	if (configuredGsrRange != ShimmerDevice.GSR_RANGE_AUTORANGE) {
            		if (gsrAdcVal < 1125 || gsrAdcVal > 3300 )
            			break;
            	}

            
            	// We need to correct the function for a non-linearity when adc ? 3163
            	if (gsrAdcVal >= 3163) {
            		resistance =  864000 - (int)(256.2748 * (gsrAdcVal - 3163)); 
            	}
            	else {
                    resistance = (int)(
                            ((0.00000037153627) * Math.pow(gsrAdcVal, 4)) +
                            ((-0.004239437)     * Math.pow(gsrAdcVal, 3)) +
                            ((17.905709)        * Math.pow(gsrAdcVal, 2)) +
                            ((-33723.8657)      * Math.pow(gsrAdcVal, 1)) + 25368044.6279);
            	}
            
                break;
            case (int)ShimmerDevice.GSR_RANGE_AUTORANGE:
                break;                
        }
        return resistance;
    } 		
    
    
    /**
     * Returns the GSR range based on the Shimmer Startup command 
     * @param command	
     * @return	Range
     */
    public static int getGsrRangeFromShimmerCommand(byte command) {
    	switch (command) {
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_1000HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_500HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_250HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_200HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_166HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_125HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_100HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_50HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_10HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_4HZ_40K: return ShimmerDevice.GSR_RANGE_HW_RES_40K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_1000HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_500HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_250HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_200HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_166HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_125HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_100HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_50HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_10HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_4HZ_287K: return ShimmerDevice.GSR_RANGE_HW_RES_287K;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_1000HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_500HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_250HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_200HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_166HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_125HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_100HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_50HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_10HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_4HZ_1M: return ShimmerDevice.GSR_RANGE_HW_RES_1M;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_1000HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_500HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_250HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_200HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_166HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_125HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_100HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_50HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_10HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	case 	ShimmerDevice.SHIMMER_COMMAND_RUNNING_4HZ_3M3: return ShimmerDevice.GSR_RANGE_HW_RES_3M3;
    	default:
    		return ShimmerDevice.GSR_RANGE_AUTORANGE;
    	
    	}

    }
//    /**
//     * Returns resistance of Shimmer device based on adc value and range
//     * @param gsrAdcVal		Value of adc
//     * @param gsrRange		Range of Shimmer device
//     * @return				resistance
//     */
//    public int GsrResistance(int gsrAdcVal, int gsrRange)	{
//        int resistance = 0;
//
//        switch (gsrRange) {
//            // curve fitting using a 4th order polynomial
//            case (int)ShimmerDevice.GSR_RANGE_HW_RES_40K:
//                resistance = (int)(
//                    ((0.0000000065995) * Math.pow(gsrAdcVal, 4)) +
//                    ((-0.000068950)    * Math.pow(gsrAdcVal, 3)) +
//                    ((0.2699)          * Math.pow(gsrAdcVal, 2)) +
//                    ((-476.9835)       * Math.pow(gsrAdcVal, 1)) + 340351.3341);
//                break;
//            case (int)ShimmerDevice.GSR_RANGE_HW_RES_287K:
//                resistance = (int)(
//                    ((0.000000013569627) * Math.pow(gsrAdcVal, 4)) +
//                    ((-0.0001650399)     * Math.pow(gsrAdcVal, 3)) +
//                    ((0.7541990)         * Math.pow(gsrAdcVal, 2)) +
//                    ((-1572.6287856)     * Math.pow(gsrAdcVal, 1)) + 1367507.9270);
//                break;
//            case (int)ShimmerDevice.GSR_RANGE_HW_RES_1M:
//                resistance = (int)(
//                    ((0.00000002550036498) * Math.pow(gsrAdcVal, 4)) +
//                    ((-0.00033136)         * Math.pow(gsrAdcVal, 3)) +
//                    ((1.6509426597)        * Math.pow(gsrAdcVal, 2)) +
//                    ((-3833.348044)        * Math.pow(gsrAdcVal, 1)) + 3806317.6947);
//                break;
//            case (int)ShimmerDevice.GSR_RANGE_HW_RES_3M3:
//                resistance = (int)(
//                    ((0.00000037153627) * Math.pow(gsrAdcVal, 4)) +
//                    ((-0.004239437)     * Math.pow(gsrAdcVal, 3)) +
//                    ((17.905709)        * Math.pow(gsrAdcVal, 2)) +
//                    ((-33723.8657)      * Math.pow(gsrAdcVal, 1)) + 25368044.6279);
//                break;
//        }
//        return resistance;
//    } 	
	
	
	

}
