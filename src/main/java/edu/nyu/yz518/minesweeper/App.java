package edu.nyu.yz518.minesweeper;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);
        builder.headless(false).run(args);
    }
}
