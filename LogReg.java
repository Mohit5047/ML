package naivebayes;

import java.util.HashMap;
import java.util.Set;

public class LogReg {
public static HashMap<String,Double> weightmap = new HashMap<String,Double>();
static double learn_rate = 0.01;
static double lambda;
public static HashMap<String,HashMap<String,Double>> spamfilemap = new HashMap<String,HashMap<String,Double>>();
public static HashMap<String,HashMap<String,Double>> hamfilemap = new HashMap<String,HashMap<String,Double>>();
public static String[] words;
public static Set<String> filelist,spamlist,hamlist;
public static double hampr,spampr;
public static int num_iterations = 50;
public  LogReg(String[] vocab,HashMap<String,HashMap<String,Double>> spammap,HashMap<String,HashMap<String,Double>>hammap,double lamda,double priorspam,double priorham)
{
	words = vocab;
	spamfilemap = spammap;
	hamfilemap = hammap;
	lambda = lamda;
	hamlist = hamfilemap.keySet();
	spamlist = spamfilemap.keySet();
	filelist.addAll(spamlist);
	filelist.addAll(hamlist);
	hampr = priorham;
	spampr = priorspam;
}

public static void train ()
{	for(int i=0;i<num_iterations;i++) {
	for(int i=0;i<words.length;i++)
	{
		double r = (Math.random() * (1-(-1))) + (-1);
		weightmap.put(words[i], r);
	}
	
	for(int i =0;i<words.length;i++)
	{
		String currentword = words[i];
		double delta_w = 0;
		for(String filename : filelist)
		{
			double classofthisfile = 0;
			double count = getcount(currentword,filename);
			if(spamlist.contains(filename))
			{
				classofthisfile = 1;
			}
			else if (hamlist.contains(filename))
			{
				classofthisfile = 0;
			}
			double result = getweightsum (filename);
			double error = classofthisfile - result;
				   delta_w = delta_w + count * error;
			
		}
		
		double newweight = weightmap.get(currentword) + learn_rate*delta_w -(learn_rate*lambda*weightmap.get(currentword));
		weightmap.replace(currentword, newweight);
		
		
	}
	
}}

public static double getcount(String currentword,String filename)
{	Double count=0.0;
	if(hamfilemap.containsKey(filename))
	{
		count = hamfilemap.get(filename).get(currentword);
	}
	else
	{
		count = spamfilemap.get(filename).get(currentword);
	}
	return count;
}

public static double getweightsum (String filename)
{	if(hamlist.contains(filename))
	{ 	double weightsum = 0;
		Set<String> filewords = hamfilemap.get(filename).keySet();
		for(String words : filewords)
		{
			weightsum = weightsum + (weightmap.get(words)) * hamfilemap.get(filename).get(words);
		
		}
		weightsum += (hampr/spampr);
		return Math.exp(weightsum)/(Math.exp(weightsum)+1);
	}
	else
	{ 	double weightsum1=0;
		Set<String> spamwords = spamfilemap.get(filename).keySet();
		for(String word : spamwords)
		{
			weightsum1 = weightsum1 + (weightmap.get(word)) * spamfilemap.get(filename).get(word);
		}
		weightsum1+=(hampr/spampr);
		return 1/(1+Math.exp(weightsum1));
	}
	
}

public static int test(HashMap<String,Double> filemap)
{	double testsum=0;
	Set<String> word = filemap.keySet();
	for(String current: word)
	{
		testsum += filemap.get(word) * weightmap.get(word);
	}
	testsum += hampr/spampr;
	if(testsum>=0)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}

}
