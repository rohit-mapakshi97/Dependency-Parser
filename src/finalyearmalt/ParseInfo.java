/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalyearmalt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rohit-pt2627
 */
public class ParseInfo
{
    private final int index;
    private final String token;
    private final String posTag;
    private Boolean isBase;
    private final Set<Integer> compoundValuesIndex; // values that co-occur like compound nouns and multi-word prepositions
    private final Set<Integer> directAttributesIndex; //immediate 
    private final HashMap<Integer,String> secondaryAttributesIndex;
    private final Set<Integer> conjunctionsIndex;
    
    public ParseInfo(int index, String token, String posTag)
    {
        this.index = index;
        this.token = token;
        this.posTag = posTag;
        this.compoundValuesIndex = new HashSet<>();
        this.directAttributesIndex = new HashSet<>();
        this.secondaryAttributesIndex = new HashMap<>();
        this.conjunctionsIndex = new HashSet<>();
        isBase = false;
    }

    public void addCompoundValuesIndex(Integer compoundValueIndex)
    {
        this.compoundValuesIndex.add(compoundValueIndex);
    }

    public void addDirectAttributesIndex(Integer directAttributeIndex)
    {
        this.directAttributesIndex.add(directAttributeIndex);
                
    }

    public void addSecondaryAttributesIndex(Integer secondaryAttributeIndex, String relation)
    {
        this.secondaryAttributesIndex.put(secondaryAttributeIndex, relation);
    }
    
    public void addConjunctionsIndex(Integer conjunctionIndex)
    {
        this.conjunctionsIndex.add(conjunctionIndex);
    }
    
    public void setAsBase()
    {
        isBase = true;
    }

    public int getIndex()
    {
        return index;
    }

    public String getToken()
    {
        return token;
    }

    public String getPosTag()
    {
        return posTag;
    }

    public Boolean isBase()
    {
        return isBase;
    }

    public Set<Integer> getCompoundValuesIndex()
    {
        return compoundValuesIndex;
    }

    public Set<Integer> getDirectAttributesIndex()
    {
        return directAttributesIndex;
    }

    public HashMap<Integer, String> getSecondaryAttributesIndex()
    {
        return secondaryAttributesIndex;
    }

    public Set<Integer> getConjunctionsIndex()
    {
        return conjunctionsIndex;
    }
    
}
