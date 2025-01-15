package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.*;

import br.ufma.ecp.Point.PointFactory;
import br.ufma.ecp.token.Token; 

public class App 
{

    
    public static void main( String[] args )
    {


        String input = "45 \"hello\" variavel + while < , if";
        Scanner scan = new Scanner (input.getBytes());
        for (Token tk = scan.nextToken(); tk.type != EOF; tk = scan.nextToken()) {
            System.out.println(tk);
        }

        /*
        Parser p = new Parser (input.getBytes());
        p.parse();
        */


        //Parser p = new Parser (fromFile().getBytes());
        //p.parse();

        /*
        String input = "489-85+69";
        Scanner scan = new Scanner (input.getBytes());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        Token tk = new Token(NUMBER, "42");
        System.out.println(tk);
        */


        Point p1 = PointFactory.createPoint(2, 3);
        Point p2 = PointFactory.createPoint(5, 7);

        // Teste dos métodos
        System.out.println("Ponto 1: " + p1);
        System.out.println("Ponto 2: " + p2);
        System.out.println("Distância entre P1 e P2: " + p1.distance(p2));
        System.out.println("Total de pontos criados: " + Point.getPointCount());
    }
}
