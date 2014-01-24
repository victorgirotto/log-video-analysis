/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modeling.and.analysis.tool;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 *
 * @author Elissa
 */
public class LogView {
    
    JTable logTable;
    DefaultTableModel logModel;
    String[] columnNames = {"Action", "Timestamp", "Solution Cards", "Vertical Proximity", "Horizontal Proximity",
        "Pre-Click Movement", "Post-Click Movement"};
    String startTime;
    String logDate = "";
    
    public LogView(File file, String path) throws FileNotFoundException
    {
        int x=0, length=0;
        
        //determine size of .csv file
        Scanner sizeScanner = new Scanner(file);
        Object temp;
        String line;
        sizeScanner.useDelimiter(",");
        while(sizeScanner.hasNextLine()) {
            temp = sizeScanner.nextLine();
            length++;
        }
        
        //create an array to store log data based on file size
        Object[][] logData = new Object[length][7];
        
        //read in log data from .csv file
        Scanner logScanner = new Scanner(file);
        while(logScanner.hasNextLine()) {
            line = logScanner.nextLine();
            logData[x] = line.split(","); 
                        
            //set date info
            if(logDate.equals(""))
            {
                String tempStr = (String) logData[x][1];
                logDate = tempStr.substring(tempStr.indexOf('h'));
            }
            
           //replace null values with empty strings
            try{
            for(int i=0; i<7; i++)
            {
                if(logData[x][i]==null)
                {
                    logData[x][i] = "";
                }
            }
            } catch (Exception e)
            {
                System.out.println(logData);
            }
            
            //remove extraneous timestamp info
            try {
            logData[x][1] = logData[x][1].toString().substring(0, logData[x][1].toString().indexOf('h'));
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            x++;
        }
        
        //set the start time as the timestamp of the first row
        startTime = (String) logData[0][1];
        
        //load the log data into the table
        logModel = new DefaultTableModel(logData, columnNames);
    }
    
    public String getStartTime()
    {
        return startTime;
    }
    
    public String getDateInfo()
    {
        return logDate;
    }
    
    public DefaultTableModel getTableModel()
    {
        return logModel;
    }
}
