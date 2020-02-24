package com.company;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable{// main class
    private boolean isRunning =false;
    private Thread thread;
    private Handler handler;
    private BufferedImage level =null;
    private Camera camera;
    public Game(){
        new Window(1000,563,"Mafia Mania",this);
        start();


        handler = new Handler();
        camera= new Camera(0,0);
        this.addKeyListener(new KeyInput(handler));
        BufferedImageLoader loader = new BufferedImageLoader();
        level=loader.loadImage("/topdown_map.png");//class not linked properly?
        loadLevel(level);
        //handler.addObject(new Player(100,100,ID.Player,handler));
        //handler.addObject(new Box( 100,100, ID.Block)); //now game knows its a block not a player

    }


    private void start(){
        isRunning=true;
        thread = new Thread(this);
        thread.start();
    }
    private void stop(){
        isRunning=false;
        try{// code below can fail
        thread.join();
        }catch (InterruptedException e){//cathces the fail
            e.printStackTrace();
        }

    }
    public void run() {//game loop
        this.requestFocus();//notch code
        long lastTime =System.nanoTime();
        double amountOfTicks =60.0;
        double ns =1000000000/ amountOfTicks;
        double delta=0;
        long timer =System.currentTimeMillis();
        int frames =0;
        while(isRunning){
            long now =System.nanoTime();
            delta+=(now-lastTime)/ ns;
            lastTime= now;
            while(delta>=1){
                tick();
                //updates++;
                delta--;
            }
            render();
            frames++;
            if (System.currentTimeMillis()-timer>1000){
                timer+=1000;
                frames=0;
                //updates =0;
            }
        }
        stop();
    }
    public void tick(){//updates everything in the game, 60 times a sec
for (i=0;i<handler.object.size();i++){
    if(handler.object.get(i).getID()==ID.Player){
        camera.tick(handler.object.get(i));
    }


}


        handler.tick();
    }
    public void render(){//makes everything in our game, thousands of times a sec
        BufferStrategy bs =this.getBufferStrategy();
        if (bs ==null){
            this.createBufferStrategy(3);//three arguments
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //////////////////////////////////////draw area below
        g.setColor(Color.red);
        g.fillRect(0,0,1000,563);
        g2d.translate(-camera.getX(),-camera.getY());


        handler.render(g);
        g2d.translate(camera.getX(),camera.getY());

        //////////////////////////////////////
        g.dispose();
        bs.show();
    }
    //loading the lvl
    private void loadLevel(BufferedImage image){
        int w = image.getWidth();
        int h = image.getHeight();
        for(int xx=0;xx<w;xx++){
            for(int yy = 0;yy<h;yy++){
                int pixel= image.getRGB(xx,yy);
                int red=(pixel>>16) & 0xff;
                int green=(pixel>>8) & 0xff;
                int blue=(pixel) & 0xff;

                if(red==255){
                    handler.addObject(new Block(xx*32,yy*32,ID.Block));
                    if(blue==255){
                        handler.addObject(new Player(xx*32,yy*32,ID.Player,handler));
                    }
                }

            }
        }

    }

    public static  void main(String args[]){
        new Game();
    }


}
