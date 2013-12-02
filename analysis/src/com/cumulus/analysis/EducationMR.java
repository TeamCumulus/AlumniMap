import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class EducationMR {
	public static class EducationMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String[] str=value.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			String location=str[1].substring(1,str[1].length()-1);
			String education=str[5].substring(1,str[5].length()-1);
			if(!"".equals(location) && !"".equals(education)){
				IntWritable flag=new IntWritable(Integer.parseInt(education));
	            context.write(new Text(location), flag);
			}
		}
	}
	public static class EducationReducer extends Reducer<Text, IntWritable, Text, Text> {
		
		@Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			int bachelor=0;
			int master=0;
			int phd=0;
			for(IntWritable val:values){
				if(val.get()==0){
					bachelor++;
				}else if(val.get()==1){
					master++;
				}else if(val.get()==2){
					phd++;
				}else{
					//DO NOTHING
				}
			}
			String str=bachelor+" "+master+" "+phd;
            context.write(key, new Text(str));
		}
	}
	public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        Job job = new Job(conf, "EducationMR");
        job.setJarByClass(EducationMR.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(EducationMapper.class);
        job.setReducerClass(EducationReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }
}