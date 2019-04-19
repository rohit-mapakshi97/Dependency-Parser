/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalyearmalt;

import java.util.Scanner;

/**
 *
 * @author rohit-pt2627
 */
public class Check
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("This is API Test");
        String query; 
        Scanner s = new Scanner(System.in);
        DependencyTree.loadModels();
        while (true)
        {         
            System.out.println("Enter Query");
            query = s.nextLine();
            DependencyTree.findDependencies(query);
        }
    }
}
