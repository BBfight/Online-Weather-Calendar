/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.control;

import java.io.Serializable;

import java.util.Date;

import java.util.Comparator;

/**
 * Class used to compare two dates.
 * 
 * @author J
 */
public class DateComparator implements Comparator<Date>, Serializable {

    @Override
    public int compare(Date d1, Date d2) {
        
        long t1 = d1.getTime();
        long t2 = d2.getTime();
        
        if (t2 > t1) {
            // The second date comes after the first one.
            return 1;
        } else if (t1 > t2) {
            // The first date comes after the second one.
            return -1;
        } else {
            // The two dates are the same.
            return 0;
        }
    }
}
