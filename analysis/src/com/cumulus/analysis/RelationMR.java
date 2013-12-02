import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class RelationMR {
	public static class RelationMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String[] str=value.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			String location=str[1].substring(1,str[1].length()-1);
			String relation=str[3].substring(1,str[3].length()-1);
			if(!"".equals(location) && !"".equals(relation)){
				IntWritable flag=new IntWritable(Integer.parseInt(relation));
				context.write(new Text(location), flag);
			}     
		}
	}
	public static class RelationReducer extends Reducer<Text, IntWritable, Text, Text> {
		
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			int single=0;
			int couple=0;
			for(IntWritable val:values){
				if(val.get()==0){
					single++;
				}else if(val.get()==1){
					couple++;
				}else{
					//DO NOTHING
				}
			}
			String str=single+" "+couple;
            context.write(key, new Text(str));
		}
	}
	public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        Job job = new Job(conf, "RelationMR");
        job.setJarByClass(RelationMR.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(RelationMapper.class);
        job.setReducerClass(RelationReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }
}