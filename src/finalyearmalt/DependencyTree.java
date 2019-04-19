/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalyearmalt;

import static finalyearmalt.FinalYearMalt.PURPLE_BRIGHT;
import static finalyearmalt.FinalYearMalt.WHITE_BRIGHT;
import java.util.Arrays;
import java.util.Scanner;
import java.util.SortedSet;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.maltparser.MaltParserService;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.syntaxgraph.DependencyStructure;
import org.maltparser.core.syntaxgraph.edge.Edge;

/**
 *
 * @author rohit-pt2627
 */
// API to use the dependency parser 
public class DependencyTree
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

    // Models 
    private static TokenizerModel tokenizerModel;
    private static TokenizerME tokenizer;
    private static POSModel posModel;
    private static POSTaggerME posTagger;
    private static MaltParserService service;

    public static void findDependencies(String query) throws Exception
    {
        // should invoke load models before using this function
        if (query.trim().equalsIgnoreCase("exit"))
        {
            System.exit(0);
        }
        
        String sentence;
        String[] tokens;
        String[] posTags;
        ParseInfo[] parseInfoArr;
        sentence = query;

        sentence = sentence.toLowerCase();
        tokens = tokenizer.tokenize(sentence);
        posTags = posTagger.tag(tokens);
        System.out.println();

        System.out.println(WHITE_BRIGHT + "Index" + "\t" + "Tokens" + "\t" + "POS Tags");
        parseInfoArr = MaltUtils.getParseInfoArray(tokens, posTags);
        Arrays.stream(parseInfoArr).forEach(x -> System.out.println(x.getIndex() + "\t" + x.getToken() + "\t" + x.getPosTag()));
        DependencyStructure graph = service.parse(MaltUtils.getConllXInput(tokens, posTags));

        SortedSet<Edge> edges = graph.getEdges();

        MaltUtils.printParseResult3(edges, tokens);
        MaltUtils.printconllX(edges, tokens, posTags);

    }

    public static void loadModels()
    {
        try
        {
            // Initialize Tokenizer
            tokenizerModel = MaltUtils.getTokenizerModel(pathToTokenizerModel);
            tokenizer = new TokenizerME(tokenizerModel);

            //Initialize POS Tagger
            posModel = MaltUtils.getPOSModel(pathToPOSModel);
            posTagger = new POSTaggerME(posModel);

            //Initialize MaltParser
            service = new MaltParserService();
            service.initializeParserModel(parserArgs);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
}
