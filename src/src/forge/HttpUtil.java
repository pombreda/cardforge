package forge;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; 
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil { 

	private static final String Boundary = "--7d021a37605f0";

    public void upload(String sURL, String file)// throws Exception
    {
    	URL url = null;
		try {
			url = new URL(sURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        HttpURLConnection theUrlConnection = null;
		try {
			theUrlConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        theUrlConnection.setDoOutput(true);
        theUrlConnection.setDoInput(true);
        theUrlConnection.setUseCaches(false);    
        theUrlConnection.setChunkedStreamingMode(1024);

        theUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="
                + Boundary);

        DataOutputStream httpOut = null;
		try {
			httpOut = new DataOutputStream(theUrlConnection.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

            File f = new File(file);
            String str = "--" + Boundary + "\r\n"
                       + "Content-Disposition: form-data;name=\"data\"; filename=\"" + f.getName() + "\"\r\n"
                       + "Content-Type: text/plain\r\n"
                       + "\r\n";

            try {
				httpOut.write(str.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            FileInputStream uploadFileReader = null;
			try {
				uploadFileReader = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            int numBytesToRead = 1024;
            int availableBytesToRead;
            try {
				while ((availableBytesToRead = uploadFileReader.available()) > 0)
				{
				    byte[] bufferBytesRead;
				    bufferBytesRead = availableBytesToRead >= numBytesToRead ? new byte[numBytesToRead]
				            : new byte[availableBytesToRead];
				    uploadFileReader.read(bufferBytesRead);
				    httpOut.write(bufferBytesRead);
				    httpOut.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				httpOut.write(("--" + Boundary + "--\r\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


        //httpOut.write(("--" + Boundary + "--\r\n").getBytes());

        try {
			httpOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			httpOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // read & parse the response
        InputStream is = null;
		try {
			is = theUrlConnection.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringBuilder response = new StringBuilder();
        byte[] respBuffer = new byte[8192];
        try {
			while (is.read(respBuffer) >= 0)
			{
			    response.append(new String(respBuffer).trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(response.toString());
    } 
    
    public String getURL(String sURL) {
    	URL url = null;
		try {
			url = new URL(sURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	InputStream is = null;
		try {
			is = url.openStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	int ptr = 0;
    	StringBuffer buffer = new StringBuffer();
    	try {
			while ((ptr = is.read()) != -1) {
			    buffer.append((char)ptr);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return buffer.toString();
    }
} 