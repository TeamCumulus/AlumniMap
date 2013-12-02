import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class FriendsMR {
	public static class FriendsMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		 
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
        	String[] str=value.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			String location=str[1].substring(1,str[1].length()-1);
			String friends=str[9].substring(1,str[9].length()-1);
        	if(!"".equals(location) && !"".equals(friends)){
        		IntWritable flag=new IntWritable(Integer.parseInt(friends));
                context.write(new Text(location), flag);
        	}
        }
	}
	public static class FriendsReducer extends Reducer<Text, IntWritable, Text, Text> {
		 
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
        	int small=0;
        	int medium=0;
        	int large=0;
        	for(IntWritable val:values){
        		if(val.get()<50){
        			small++;
        		}else if(val.get()>=50 && val.get()<=250){
        			medium++;
        		}else{
        			large++;
        		}
        	}
        	String str=small+" "+medium+" "+large;
            context.write(key, new Text(str));
        }
	}
	public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        Job job = new Job(conf, "FriendsMR");
        job.setJarByClass(FriendsMR.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(FriendsMapper.class);
        job.setReducerClass(FriendsReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }
}