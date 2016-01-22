/**
 * 
 */
package com.its.core.module.filescan.violation;

/**
 * 创建日期 2012-12-13 下午01:13:24
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class VideoFileNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5685972771901292316L;
	
	private String videoId;

    public VideoFileNotFoundException()
    {
        videoId = null;
    }

    public VideoFileNotFoundException(String message)
    {
        super(message);
        videoId = null;
    }

    public VideoFileNotFoundException(Throwable cause)
    {
        super(cause);
        videoId = null;
    }

    public VideoFileNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
        videoId = null;
    }

    public VideoFileNotFoundException(String videoId, String message)
    {
        super(message);
        this.videoId = videoId;
    }

    public String getVideoId()
    {
        return videoId;
    }

    public void setVideoId(String videoId)
    {
        this.videoId = videoId;
    }

}
