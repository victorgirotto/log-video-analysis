/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modeling.and.analysis.tool;

import com.sun.jna.NativeLibrary;
import java.awt.Canvas;
import java.awt.Color;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 *
 * @author Elissa
 */
public class VideoView {
    
    private Canvas ourCanvas = new Canvas();
    private String mediaPath = "";        
    private EmbeddedMediaPlayer mediaPlayer;
    
    VideoView(String vlcPath,String mediaURL, int width, int height)
    {
        this.mediaPath = mediaURL;
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath);
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();        
        ourCanvas.setBackground(Color.black);
        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        mediaPlayer.setPause(true);
        mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(ourCanvas));
        ourCanvas.setSize(width,height);
        ourCanvas.setVisible(true);
    }
    
    public Canvas getVideoDisplay()
    {
        return ourCanvas;
    }
    
    public long getVideoLength()
    {
        long length = mediaPlayer.getLength();
        return length;
    }
        
    public void executePreviousChapter()
    {
        mediaPlayer.previousChapter();
    }
    
    public void executeNextChapter()
    {
        mediaPlayer.nextChapter();
    }
    
    public void executeRewind()
    {
        long time = mediaPlayer.getTime();
        time = time - 100;
        mediaPlayer.setTime(time);
    }
    
    public void executeFastForward()
    {
        mediaPlayer.nextFrame();
    }
    
    public void executePause()
    {
        mediaPlayer.pause();
    }
    
    public void executePlay()
    {
        mediaPlayer.play();
    }
    
    public void executeStop()
    {
        mediaPlayer.stop();
    }
    
    public void setNewPosition(int percentComplete)
    {
        float newPosition = (float) (percentComplete / 100.0);
        System.out.println(newPosition);
        mediaPlayer.setPosition(newPosition);
    }
    
    public long getTimestamp()
    {
        return mediaPlayer.getTime();
    }
    
    public void run()
    {
        mediaPlayer.playMedia(mediaPath);
    }
    

}
