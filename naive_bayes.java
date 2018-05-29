package naivebayes;
import naivebayes.LogReg.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.*;
public class main {

public static String[] gettext(File[] list) throws IOException
{	String text = null;
	for(int i=0;i<list.length;i++)
	{
		if(list[i].isFile()&&list[i].getName().endsWith(".txt"))
		{
			Scanner scan = new Scanner(list[i]);
			while(scan.hasNextLine())
			{
				text = text + " " + scan.nextLine();
			}
		}
	}
	text = text.replaceAll("^[A-Za-z]+", " ");
	text = text.toLowerCase();
	String[] words = text.split("\\s+");
	return words;
}

public static HashMap<String,HashMap<String,Double>> getfilemap (File[] filelist)
{	HashMap<String,HashMap<String,Double>> filemap = new HashMap<String,HashMap<String,Double>> ();
 	
	
	for(int i =0;i<filelist.length;i++)
	{	File file = filelist[i];
		HashMap <String,Double> filedata = new HashMap<String,Double>();
		String text = null;
		String[] words = null;
 	try {
		Scanner scan = new Scanner(file);
		while(scan.hasNextLine())
		{text = text + " " + scan.nextLine();}
		scan.close();
		text.replaceAll("^[A-Za-z]+", " ");
		text = text.toLowerCase();
		words = text.split("\\s+");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	for(int j=0;j<words.length;j++)
	{
		if(filedata.containsKey(words[j]))
		{
			double b = filedata.get(words[j]) + 1;
			filedata.replace(words[j], b);
		}
		else
		{
			filedata.put(words[j], (double) 1);
		}
	}
	filemap.put(file.getName(), filedata);
	}
	return filemap;
}

public static Map<String, Double> prprob(String[] words)
{
	Map<String,Double> a = new HashMap<String,Double>();
	for(int i =0;i<words.length;i++)
	{
		if(a.containsKey(words[i]))
		{
			double b = a.get(words[i])+1;
			a.replace(words[i], b); //increase count
		}
		else
		{
			a.put(words[i], (double) 1); //if not in list then add
		}
	}
	return a;
}
	
public static List<String> generatevocab(Map<String,Double> a,Map<String,Double>b)
{
	List<String> c =  a.keySet().stream().collect(Collectors.toList());
	List<String> d = b.keySet().stream().collect(Collectors.toList());
	if(c.size()>d.size())
	{
		for(int i = 0;i<d.size();i++)
		{
			if(c.contains(d.get(i))==false)
			{
				c.add(d.get(i));
			}
		}
		Collections.sort(c);
		return c;
	}
	else
	{
		for(int i = 0;i<c.size();i++)
		{
			if(d.contains(c.get(i))==false)
			{
				d.add(c.get(i));
			}
		}
		Collections.sort(d);
		return d;
	}
	}

public static Map<String,Double> generatepriorprob (Map<String,Double> a, List<String> b)
{
	List<String> c = a.keySet().stream().collect(Collectors.toList());
	double sum = 0;
	for(int i=0;i<c.size();i++)
	{
		sum += a.get(c.get(i));
	}
	sum += (double)b.size();
	for(int i=0;i<c.size();i++)
	{
		double prob = (a.get(c.get(i))+1)/sum;
		a.replace(c.get(i), prob);
	}
	return a;
}

public static HashMap<String,HashMap<String,Double>> removestop(String[] stopword, File[] filelist,HashMap<String,HashMap<String,Double>> filemap)
{	for(int i =0;i<filelist.length;i++)
{
	String file = filelist[i].getName();
	HashMap<String,Double> value = filemap.get(file);
	for(int j=0;j<stopword.length;j++)
	{
		if(value.containsKey(stopword[i]))
		{
			value.remove(stopword[i]);
		}
	}
	
}
return filemap;
	
	
	
}
public static double testfile(File file, Map<String,Double> ham, Map<String,Double> spam,double priorham,double priorspam) throws Exception
{	double accuracy =0;String content = "";
	Scanner scan = new Scanner (file);
	while(scan.hasNextLine())
	{
		content = content + " " + scan.nextLine();
	}
	content = content.replaceAll("^[A-Za-z]+", " ");
	content = content.toLowerCase();
	String[] words = content.split("\\s+");
	double probham = Math.log(1),probspam = Math.log(1);
	for(int i=0;i<words.length;i++)
	{
	if(ham.containsKey(words[i]))
	{
		probham = probham + Math.log(ham.get(words[i]));
	}
}
for (int i=0;i<words.length;i++)
{
	if(spam.containsKey(words[i]))
	{
		probspam = probspam + Math.log ( spam.get(words[i]));
	}
}
probham = probham + Math.log(priorham);
probspam = probspam + Math.log(priorspam);
if(probham >probspam)
{
	String d = "/Users/mohit/Documents/Sem 2/ML/test/ham/"+file + ".txt";
	File check = new File(d);
	if (check.exists())
	{
		accuracy ++;
	}
}

else if(probspam>probham)
{
	String d = "/Users/mohit/Documents/Sem 2/ML/test/spam/"+file+".txt";
	File check = new File(d);
	if(check.exists())
	{
		accuracy ++;
	}
}
return accuracy;
}

public static void main(String[] args) throws Exception
{
String trainham = "/Users/mohit/Documents/Sem 2/ML/train/ham";
String trainspam = "/Users/mohit/Documents/Sem 2/ML/train/spam";
File foldertrham = new File(trainham) ;
File foldertrspam = new File(trainspam);
File[] trhamlist = foldertrham.listFiles();
File[] trspamlist = foldertrspam.listFiles();
int nspam =  trspamlist.length;
int nham = trhamlist.length ;
double priorham = nham/(nham+nspam);//prior probability of ham
double priorspam = nspam/(nham+nspam);//prior probability of spam
String[] trspamtext = gettext(trspamlist);
String[] trhamtext = gettext(trhamlist);

Map<String,Double> trhampr = prprob(trhamtext); //get map of ham words from training data and their count
Map<String,Double> trspampr = prprob(trspamtext); // get map of spam words from training data and their count
List<String> vocab = generatevocab(trhampr,trspampr);//get complete list of vocabulary 
Map<String,Double>trhampr1 = generatepriorprob (trhampr,vocab); //get prior conditional probability of each term of ham
Map<String,Double>trspampr1 = generatepriorprob(trspampr,vocab); // get prior conditional probability of each term of spam
String testham = "/Users/mohit/Documents/Sem 2/ML/test/ham";
String testspam = "/Users/mohit/Documents/Sem 2/ML/test/spam";
File teham = new File (testham);
File tespam = new File(testspam);
File[] tehamlist = teham.listFiles();
File[] tespamlist = tespam.listFiles();
File[] testlist = ArrayUtils.addAll(tehamlist, tespamlist);
double nbmultiaccuracy = 0;
for(int i=0; i<testlist.length;i++)
{	File file = testlist[i];
	if(file.isFile() && file.getName().endsWith(".txt"))
	{
		nbmultiaccuracy += testfile(file,trhampr1,trspampr1,priorham,priorspam);
	}
}
nbmultiaccuracy = nbmultiaccuracy / testlist.length;
System.out.println("Accuracy of NB Multinomial = " + nbmultiaccuracy);

//Logistic Regression
HashMap<String,HashMap<String,Double>> spamfilemap = getfilemap(trspamlist);
HashMap<String,HashMap<String,Double>> hamfilemap = getfilemap(trhamlist);
double lamda;
System.out.println("Enter value of L2 lamda:");
Scanner scan = new Scanner(System.in);
lamda = scan.nextDouble();
scan.close();
String a = vocab.toString();
String[] vocab1 = a.split("\\s+");
LogReg lr = new LogReg (vocab1,spamfilemap,hamfilemap,lamda,priorspam,priorham);
lr.train();
HashMap<String,HashMap<String,Double>> hamtest = getfilemap(tehamlist);
HashMap<String,HashMap<String,Double>> spamtest = getfilemap(tespamlist);
System.out.println("Spam test");
int spam_count = 0;
for(int i=0;i<tespamlist.length;i++)
{
	HashMap<String,Double> test = spamtest.get(tespamlist[i].getName());
	int spam = lr.test(test);
	if(spam ==1)
	{
		spam_count++;
	}
}
double spam_acc = spam_count/tespamlist.length;
System.out.println("Accuracy of Spam test = "+spam_acc);
System.out.println("Ham Test");
int ham_count=0;
for(int i =0;i<tehamlist.length;i++)
{
	HashMap<String,Double> test = hamtest.get(tehamlist[i].getName());
	int ham = lr.test(test);
	if(ham==0)
	{
		ham_count++;
	}
}
double ham_acc = ham_count/tehamlist.length;
System.out.println("Accuracy of Ham test = "+ham_acc);
String stopwords = "/Users/mohit/Documents/Sem 2/Ml/Assignment2/stopwords.txt";
//Removing Stop Words
File stopfile = new File (stopwords);
String stoptext = "";
Scanner sc = new Scanner (stopfile);
while(sc.hasNextLine())
{
	stoptext = stoptext + " " + sc.nextLine();
}
sc.close();
stoptext = stoptext.replaceAll("^[A-Za-z]+", " ");
stoptext = stoptext.toLowerCase();
String[] stopword = stoptext.split("\\s+");
for(int i=0;i<stopword.length;i++)
{
	if(trhampr.containsKey(stopword[i]))
	{
		trhampr.remove(stopword[i]);
	}
	if(trspampr.containsKey(stopword[i]))
			{
				trspampr.remove(stopword[i]);
			}
	if(vocab.contains(stopword[i]))
	{
		vocab.remove(stopword[i]);
	}
}
trhampr = generatepriorprob(trhampr,vocab);
trspampr = generatepriorprob(trspampr,vocab);
double nbstopaccuracy =0;
for(int i=0;i<testlist.length;i++)
{
	File file = testlist[i];
	if(file.isFile() && file.getName().endsWith(".txt"))
	{
		nbstopaccuracy += testfile(file,trhampr,trspampr,priorham,priorspam);
	}
}
System.out.println ("Accuracy of Nb with Stop removal = "+nbstopaccuracy);
HashMap<String,HashMap<String,Double>> spamstoptr = removestop(stopword,tespamlist,spamfilemap);
HashMap<String,HashMap<String,Double>> hamstoptr = removestop(stopword,tehamlist,hamfilemap);
String b = vocab.toString();
b = b.toLowerCase();
String[] vocab2 = b.split("\\s+");
LogReg lr2 = new LogReg(vocab2,spamstoptr,hamstoptr,lamda,priorspam,priorham);
lr2.train();
HashMap<String,HashMap<String,Double>> hamtest1 = removestop(stopword,tehamlist,hamtest);
HashMap<String,HashMap<String,Double>> spamtest1 = removestop(stopword,tespamlist,spamtest);
System.out.println("Spam test");
int spam_count1 = 0;
for(int i=0;i<tespamlist.length;i++)
{
	HashMap<String,Double> test = spamtest1.get(tespamlist[i].getName());
	int spam = lr.test(test);
	if(spam ==1)
	{
		spam_count++;
	}
}
double spam_acc1 = spam_count1/tespamlist.length;
System.out.println("Accuracy of Spam test = "+spam_acc1);
System.out.println("Ham Test");
int ham_count1=0;
for(int i =0;i<tehamlist.length;i++)
{
	HashMap<String,Double> test = hamtest1.get(tehamlist[i].getName());
	int ham = lr.test(test);
	if(ham==0)
	{
		ham_count++;
	}
}
double ham_acc1 = ham_count1/tehamlist.length;
System.out.println("Accuracy of Ham test = "+ham_acc1);

}
}
