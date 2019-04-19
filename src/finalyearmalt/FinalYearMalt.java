/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalyearmalt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.maltparser.MaltParserService;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.io.dataformat.DataFormatException;
import org.maltparser.core.symbol.SymbolTable;
import org.maltparser.core.syntaxgraph.DependencyStructure;
import org.maltparser.core.syntaxgraph.LabelSet;
import org.maltparser.core.syntaxgraph.edge.Edge;
import sun.net.www.content.audio.x_aiff;

/**
 *
 * @author rohit-pt2627
 */
public class FinalYearMalt
{

    private static String parserArgs = "-c corpora -m parse -w /home/local/ZOHOCORP/rohit-pt2627/workspace/FinalYear -lfi parser.log";
    private static String pathToPOSModel = "/home/local/ZOHOCORP/rohit-pt2627/workspace/FinalYear/FinalYearMalt/res/en-pos-umbc.bin";
    private static String pathToTokenizerModel = "/home/local/ZOHOCORP/rohit-pt2627/workspace/FinalYear/FinalYearMalt/res/en-zreports-tokenizer.bin";

    //Colors
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    private final static Logger LOGGER = Logger.getLogger(FinalYearMalt.class.getName());
    
    public static void main(String[] args) throws Exception
    {
        if (args.length != 0)
        {
            //parser args
            //path to POS model
            //path to Tokenizer Model

        }

        TokenizerModel tokenizerModel;
        TokenizerME tokenizer;
        POSModel posModel;
        POSTaggerME posTagger;
        MaltParserService service;
        try
        {
            // Initialize Tokenizer
            Thread.sleep(2000);
            tokenizerModel = getTokenizerModel(pathToTokenizerModel);
            tokenizer = new TokenizerME(tokenizerModel);

            //Initialize POS Tagger
            Thread.sleep(2000);
            posModel = getPOSModel(pathToPOSModel);
            posTagger = new POSTaggerME(posModel);

            //Initialize MaltParser
            service = new MaltParserService();
            service.initializeParserModel(parserArgs);

            Boolean isCustom = false;
            String sentence;
            String[] tokens;
            String[] posTags;
            ParseInfo[] parseInfoArr;
            System.out.println(PURPLE_BRIGHT + "Enter the test String");
            Scanner in = new Scanner(System.in);
            sentence = in.nextLine();
            do
            {

                sentence = sentence.toLowerCase();
                tokens = tokenizer.tokenize(sentence);
                posTags = posTagger.tag(tokens);
                System.out.println();
               
                System.out.println(WHITE_BRIGHT+"Index"+"\t"+"Tokens"+"\t"+"POS Tags");
                parseInfoArr = getParseInfoArray(tokens, posTags);
                Arrays.stream(parseInfoArr).forEach(x -> System.out.println(x.getIndex()+"\t"+x.getToken()+"\t"+x.getPosTag()));
                DependencyStructure graph = service.parse(getConllXInput(tokens, posTags));

                SortedSet<Edge> edges = graph.getEdges();

                printParseResult3(edges, tokens);
                printconllX(edges, tokens, posTags);
                do
                {
                    System.out.println(PURPLE_BRIGHT + "Enter the test String");
                    sentence = in.nextLine();
                }
                while (sentence.isEmpty());
            }
            while (!sentence.equalsIgnoreCase("exit"));
            service.terminateParserModel();

        }
        catch (MaltChainedException e)
        {
            System.err.println("MaltParser exception: " + e.getMessage());
        }
    }

    private static void printParseResult3(SortedSet<Edge> edges, String[] tokens) throws MaltChainedException, Exception
    {
        String dependency = null;
        System.out.println();
        System.out.println(RED_BRIGHT+"Dependency Relations:");
        for (Edge edge : edges)
        {
            Integer sourceIndex = edge.getSource().getIndex();
            Integer targetIndex = edge.getTarget().getIndex();

            LabelSet labelSet = edge.getLabelSet();

            if (labelSet != null)
            {
                for (SymbolTable st : labelSet.keySet())
                {
                    dependency = edge.getLabelSymbol(st);
                }
            }
            System.out.println();

            if (sourceIndex == 0)
            {
                System.out.print(WHITE_BRIGHT+ "ROOT(" + sourceIndex + ")" + "->" + tokens[targetIndex - 1] + "(" + targetIndex + ")");
            }
            else if (targetIndex == 0)
            {
                throw new Exception("Target index cannot be zero");
            }
            else
            {
                System.out.print(dependency + "(" + tokens[sourceIndex - 1] + "(" + sourceIndex + ")" + "->" + tokens[targetIndex - 1] + "(" + targetIndex + "))");
            }

        }
    }

    private static ParseInfo[] getParseInfoArray(String[] tokens, String[] posTags)
    {
        Integer tokenLength = tokens.length;
        ParseInfo[] parseInfoArray = new ParseInfo[tokenLength];
        for (int i = 0; i < tokenLength; i++)
        {
            parseInfoArray[i] = new ParseInfo(i, tokens[i], posTags[i]);
        }
        return parseInfoArray;
    }

    private static String[] getConllXInput(String[] tokens, String[] posTags) throws IOException
    {
        Integer tokenCount = tokens.length;
        String[] modifiedTokens = new String[tokenCount];
        for (int i = 0; i < tokenCount; i++)
        {
            modifiedTokens[i] = (i + 1) + "\t" + tokens[i] + "\t" + tokens[i] + "\t" + posTags[i] + "\t" + posTags[i] + "\t" + tokens[i];
        }

        return modifiedTokens;
    }

    

    private static POSModel getPOSModel(String pathToPOSModel) throws IOException
    {

        System.out.println(BLUE_BRIGHT + "ModelLoader - Loading POS model. Path - " + pathToPOSModel);
        POSModel posModel = null;
        InputStream modelInStream = null;

        try
        {
            modelInStream = new FileInputStream(pathToPOSModel);
            posModel = new POSModel(modelInStream);
        }
        catch (FileNotFoundException ex)
        {
            LOGGER.log(Level.SEVERE, "Unable to load pos model. ", ex);
            throw ex;
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Error loading pos model. ", ex);
            throw ex;
        }
        finally
        {
            if (modelInStream != null)
            {
                try
                {
                    modelInStream.close();
                }
                catch (IOException ex)
                {
                    LOGGER.log(Level.SEVERE, "Unable to close InputStream for POS model. ", ex);
                }
            }

        }

        return posModel;
    }

    private static TokenizerModel getTokenizerModel(String pathToTokenizerModel) throws IOException
    {
        System.out.println(BLUE_BRIGHT + "ModelLoader -   Loading Tokenizer Model" + pathToTokenizerModel);
        TokenizerModel tokenizerModel;
        InputStream modelInStream = null;

        try
        {
            modelInStream = new FileInputStream(pathToTokenizerModel);
            tokenizerModel = new TokenizerModel(modelInStream);
        }
        catch (FileNotFoundException ex)
        {
            LOGGER.log(Level.SEVERE, "Unable to find probability model. ", ex);
            throw ex;
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Error loading tokenizer model. ", ex);
            throw ex;
        }
        finally
        {
            if (modelInStream != null)
            {
                try
                {
                    modelInStream.close();
                }
                catch (IOException ex)
                {
                    LOGGER.log(Level.SEVERE, "Unable to close InputStream for tokenizer model. ", ex);
                }
            }
        }

        return tokenizerModel;
    }


    /*
1	ravi	ravi	NN	NN	_	2	compound	_	_
2	shankar	shankar	NN	NN	_	0	root	_	_
3	,	,	,	,	_	2	punct	_	_
4	sai	sai	NN	NN	_	5	compound	_	_
5	kiran	kiran	NN	NN	_	2	conj	_	_
6	,	,	,	,	_	2	punct	_	_
7	raja	raja	NN	NN	_	8	compound	_	_
8	ram	ram	NN	NN	_	2	conj	_	_
9	,	,	,	,	_	2	punct	_	_
10	satish	satish	JJ	JJ	_	11	amod	_	_
11	kumar	kumar	NN	NN	_	13	compound	_	_
12	created	create	VBN	VBN	_	13	amod	_	_
13	leads	lead	NNS	NNS	_	2	conj	_	_

     */
    private static void printconllX(SortedSet<Edge> edges, String[] tokens, String[] posTags) throws MaltChainedException, Exception
    {
        PrintWriter pw;
        try
        {
            System.out.println();
            System.out.println(RED_BRIGHT+"\nConll-X Format : ");
            pw = new PrintWriter("/home/local/ZOHOCORP/rohit-pt2627/workspace/FinalYear/Temp/dependency.conll");
            String dependency = null;
            String[] modifiedTokens = new String[edges.size()];
            int i = 0;
            for (Edge edge : edges)
            {
                Integer sourceIndex = edge.getSource().getIndex();
                Integer targetIndex = edge.getTarget().getIndex();

                LabelSet labelSet = edge.getLabelSet();

                if (labelSet != null)
                {
                    for (SymbolTable st : labelSet.keySet())
                    {
                        dependency = edge.getLabelSymbol(st);
                    }
                }

                modifiedTokens[i] = targetIndex + "\t" + tokens[targetIndex - 1] + "\t" + tokens[targetIndex - 1] + "\t" + posTags[targetIndex - 1] + "\t" + posTags[targetIndex - 1] + "\t" + "_" + "\t" + String.valueOf(sourceIndex) + "\t" + dependency + "\t" + "_" + "\t" + "_";
                pw.println(modifiedTokens[i]);
                System.out.println(WHITE_BRIGHT+modifiedTokens[i]);
            }
            pw.println();
            pw.close();
            System.out.println();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Runtime.getRuntime().exec("java -jar /home/local/ZOHOCORP/rohit-pt2627/workspace/FinalYear/Demo/lib/MaltEvalProj.jar -g /home/local/ZOHOCORP/rohit-pt2627/workspace/FinalYear/Temp/dependency.conll -v 1 ");
        }

    }
}
 