/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extraction;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalStructureConversionUtils;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 *
 * @author rohit-pt2627
 */
public class Extraction
{

    public static String text = "total sales and average sales";

    public static void main(String[] args)
    {
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        CoreDocument document;
        CoreSentence sentence;
        SemanticGraph dependencyParse;

        String conll;

        String readString;
        PrintWriter pw;
        BufferedReader br;

        int i = 0;
        try
        {
            br = new BufferedReader(new FileReader("/home/local/ZOHOCORP/rohit-pt2627/workspace/Parser/Parser/AllTrainingData/roh.txt"));
            pw = new PrintWriter("/home/local/ZOHOCORP/rohit-pt2627/workspace/FinalYear/training/train_test.conll");
            while ((readString = br.readLine()) != null)
            {
                i++;
//                System.out.println(readString);
                document = new CoreDocument(readString);
                pipeline.annotate(document);
                sentence = document.sentences().get(0);
                dependencyParse = sentence.dependencyParse();
                conll = GrammaticalStructureConversionUtils.dependenciesToCoNLLXString(dependencyParse.typedDependencies(), sentence.coreMap());
//                System.out.println(dependencyParse.typedDependencies().toString());
                pw.println(conll);
                if (i % 1000 == 0)
                {
                    System.out.println("Processed +" + i + " sentences");
                }

            }
            br.close();
            pw.close();
            System.out.println("Done...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
