/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;

public class AvatarChannel extends AvatarMappedObject{

    private AvatarRAM avatarRAMReference; 
    private String referenceDiagram ;
	private String channelName ;
    private int no_cluster;
    private int  monitored;

    public AvatarChannel(String _referenceDiagram,  String _channelName, AvatarRAM _avatarRAMReference, int _no_cluster, int _monitored){
      referenceDiagram =  _referenceDiagram;
      channelName = _channelName;
      avatarRAMReference = _avatarRAMReference;
      no_cluster = _no_cluster;
      monitored = _monitored;
    }

    public AvatarRAM getAvatarRAMReference(){
      return  avatarRAMReference;
    }

    public int getRAMNo(){
      return avatarRAMReference.getNo_ram();
    }

    public String getReferenceDiagram(){
      return referenceDiagram;
    }

    /*public String  getChannelName(){ 
	 String newChannelName = channelName.substring(0,10)+channelName.substring(channelName.length()-10,channelName.length()-1);
      return newChannelName;
      } */
    
    public String  getChannelName(){      
      return channelName;
      } 

    public int getNo_cluster(){
      return no_cluster;
    } 

    public int getMonitored(){
      return monitored;
    } 

}