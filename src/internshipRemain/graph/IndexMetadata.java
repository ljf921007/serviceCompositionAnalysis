package internshipRemain.graph;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 */
public class IndexMetadata implements Comparable {
     private long index;
     private String origin;
     private String namespace;
     private long ingestTime; // in seconds
     private String ingestTimeStr; // ingestTime in human readable format
     private String type;
        
     public IndexMetadata(long index, String origin, String namespace, long ingestTime, String ingestTimeStr, String type) {
        this.index = index;
        this.origin = origin;
        this.namespace = namespace;
        this.ingestTime = ingestTime;
        this.ingestTimeStr = ingestTimeStr;
        this.type = type;
     }
     
     public String getType() {
    	 return type;
     }
     
     public long getIndex() {
         return this.index;
     }
     
     public String getOrigin() {
         return this.origin;
     }
     
     public String getNamespace() {
         return this.namespace;
     }
     
     public long getIngestTime() {
         return this.ingestTime;
     }
     
     public String getIngestTimeStr() {
         return this.ingestTimeStr;
     }
     
      @Override
    public int compareTo(Object obj) {
        IndexMetadata other = (IndexMetadata) obj;
         if(this.index < other.index) {
             return -1;
         } else if(this.index > other.index) {
             return 1;
         } else {
             return 0;
         } 
     }
     
     public String toString() {
         return "FrameMetadata<" + "index=" + this.index + ",namespace=" + this.namespace  + ",origin=" + this.origin + 
        		 ",type=" + this.type + ">";
     }

   
     
}

