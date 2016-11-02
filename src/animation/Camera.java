package animation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.LineBorder;

import dataTypes.*;

public class Camera extends JScrollPane implements Runnable , MouseMotionListener {
	private final int 				DELAY = 25;
	private Thread 					cameraThread;
	private ArrayList<Component>  	scenes;
	
	private DVector2D		cameraTarget;
	private DVector2D		cameraPosition;
	private DVector2D		cameraValidPosition;
	private double			maxMoveSpeed = 50;
	private double			minMoveSpeed = 0.5;
	
	private boolean			fixedTarget = true;
	
	
	public Camera( Component viewPort , int x , int y ) {
		super();
		this.scenes = new ArrayList<Component>();
		this.scenes.add(viewPort);
		this.cameraTarget = new DVector2D( x , y );
		this.cameraPosition = new DVector2D( x , y );
		this.cameraValidPosition = new DVector2D( 0 , 0 );
		this.setViewportView( viewPort );
		this.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // ???
		//this.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
	}

    @Override
    public void addNotify() {
        super.addNotify();
        
        this.setAutoscrolls( true );
        this.addMouseMotionListener(this);

        this.cameraThread = new Thread(this);
        this.cameraThread.start();
    }	
	
    private void threadCycle( long passed ) {
    	for( Component comp : this.scenes ) {
    		if (comp instanceof Animable ) {
    			((Animable) comp).passTime( passed );
    		}
    	}
    	
    	if( this.fixedTarget == true ) {
	    	// Set Position
	    	DVector2D offset = new DVector2D( this.getSize().getWidth() / 2 , this.getSize().getHeight() / 2 );
	    	DVector2D effectiveTarget = new DVector2D( this.cameraTarget.x - offset.x , this.cameraTarget.y - offset.y );
	    	
			// Boundaries
			if( effectiveTarget.x < 0 ) {
				effectiveTarget.x = 0;
			} else if ( effectiveTarget.x > 100000 ) {
				effectiveTarget.x = 100000;
			}
			
			if( effectiveTarget.y < 0 ) {
				effectiveTarget.y = 0;
			} else if ( effectiveTarget.y > 100000 ) {
				effectiveTarget.y = 100000;
			} 
	    	
	    	// Calculate Move Speed
	    	DVector2D distance = DVector2D.getDistanceVector( this.cameraPosition , effectiveTarget );
	    	if( distance.getModulus() > 1 ) {
	    		DVector2D moveSpeed = new DVector2D( distance.x / 50 , distance.y / 50 );
	    		
	    		if( Math.abs( this.cameraPosition.x - effectiveTarget.x ) >= 1 ) {
		    		if( moveSpeed.x < this.minMoveSpeed ) {
		    			moveSpeed.x = this.minMoveSpeed;
		    		} else if ( moveSpeed.x > this.maxMoveSpeed ) {
		    			moveSpeed.x = this.maxMoveSpeed;
		    		}
	    		} else {
	    			moveSpeed.x = 0;
	    		}	
	    		
	    		if( Math.abs( this.cameraPosition.y - effectiveTarget.y ) >= 1 ) {
		    		if( moveSpeed.y < this.minMoveSpeed ) {
		    			moveSpeed.y = this.minMoveSpeed;
		    		} else if ( moveSpeed.y > this.maxMoveSpeed ) {
		    			moveSpeed.y = this.maxMoveSpeed;
		    		}
	    		} else {
	    			moveSpeed.y = 0;
	    		}
	
	    		this.cameraPosition.x += moveSpeed.x ;
	    		this.cameraPosition.y +=  moveSpeed.y ;
	    	}
    		
    		// Finally Set   
    		this.getViewport().setViewPosition( new Point( (int)this.cameraPosition.x , (int)this.cameraPosition.y ));
    	}
    	
    	Toolkit.getDefaultToolkit().sync();
    }	
	
	@Override
	public void run() {
		
        long beforeTime, timeDiff, sleep;
        timeDiff = 0;

        beforeTime = System.currentTimeMillis();

        while (true) {

        	threadCycle(DELAY);
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            beforeTime = System.currentTimeMillis();
            sleep = DELAY - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }
           
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }   
        }	
	}
	
	public void setTarget( int x , int y ) {
		this.cameraTarget.x = x;
		this.cameraTarget.y = y;
		
        /*
        this.getViewport().invalidate();
        this.getViewport().revalidate();
        this.getViewport().repaint();
        this.invalidate();
        this.revalidate();
        this.repaint();
        */
	}
	
	public void setIsFixedOnTarget( boolean mode ) {
		this.fixedTarget = mode;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        
        //this.setTarget(e.getX(), e.getY());
        
        //((JPanel)e.getSource()).scrollRectToVisible(r);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}