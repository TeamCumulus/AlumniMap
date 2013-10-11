package com.cumulus.dataAnalyze;

import com.cumulus.webcrawler.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class Analysis {
	
	//Location as a key
	 class LocationMap extends Mapper<LongWritable, Text, Text, User >{
		 //for location based mapping
		public void map(LongWritable key, Text value, Context context) 
				throws IOException, InterruptedException{
		      //get txt node retrieve the location and put the person for the map
			   Text Location = new Text();
			   //Separate lines get the object
			   while(true)
			   {
				   String l = value.toString();
				   User u = new User();
				   //u.set...???
				   Location.set(l);
			       context.write(Location,u);
			   }

		       }
	}
	 
	class LocationReduce extends Reducer <Text, User, Text, ArrayList<User>> {		    
		   public void reduce(Text key, Iterable<User> values, Context context) 
				   throws IOException, InterruptedException
		   {
			  List<User> locationList  = new ArrayList<User>();
			  for (User val : values) {

				  locationList.add(val);

				}

				context.write(key, (ArrayList<User>) locationList);
		   };
		}
	
	//Work as a key
	class WorkMap extends Mapper<LongWritable, Text, Text, User > {
		
		 //for Work based mapping
		public void map(LongWritable key, Text value, Context context) 
				throws IOException, InterruptedException{
		      //get txt node retrieve the location and put the person for the map
			   Text Work = new Text();
			   //Separate lines get the object
			   while(true)
			   {
				   String w = value.toString();
				   User u = new User();
				   //u.set...???
				   Work.set(w);
			       context.write(Work,u);
			   }

		       }
	}
   
	 class WorkReduce extends Reducer <Text, User, Text, ArrayList<User>> {		
		 
		 public void reduce(Text key, Iterable<User> values, Context context) 
				   throws IOException, InterruptedException
		   {
			  List<User> workList  = new ArrayList<User>();
			  for (User val : values) {

				  workList.add(val);

				}

				context.write(key, (ArrayList<User>) workList);
		   };
	 }
}
