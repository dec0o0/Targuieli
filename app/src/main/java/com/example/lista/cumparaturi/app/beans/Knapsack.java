package com.example.lista.cumparaturi.app.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookproritena on 1/2/17.
 */

public class Knapsack
{
    private int howMane, maxW;
    private List<Integer> weights, values;

    public Knapsack(int howMane, int maxW, List<Integer> weights, List<Integer> values) {
        this.howMane = howMane;
        this.maxW = maxW;
        this.weights = new ArrayList<>(weights);
        this.values = new ArrayList<>(values);
    }

    int max(int a, int b) { return (a > b)? a : b; }

    public int[] knapSack(int w, int wt[], int val[], int n)
    {
        int NEG_INF = Integer.MIN_VALUE;
        int[][] m = new int[n + 1][w + 1];
        int[][] sol = new int[n + 1][w + 1];

        for(int i = 1; i <= n; ++i){
            for(int j = 0; j <= w; ++j)
            {
                int m1 = m[i - 1][j],
                        m2 = NEG_INF;

                if(j > wt[i])
                    m2 = m[i - 1][j - wt[i]] + val[i];

                m[i][j] = Math.max(m1, m2);
                sol[i][j] = m2 > m1 ? 1 : 0;
            }
        }

        int[] selected = new int[n + 1];
        for(int nn = n, ww = w; nn > 0; --nn){
            if(sol[nn][ww] != 0){
                selected[nn] = 1;
                ww = ww - wt[nn];
            }
            else{
                selected[nn] = 0;
            }
        }

        return selected;
    }
}