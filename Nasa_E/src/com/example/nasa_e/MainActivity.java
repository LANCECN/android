package com.example.nasa_e;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
	private static int iTimes = 1;
	private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh(iTimes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onRefreshClicked(View view)
    {
 
    	iTimes = (++iTimes) % 5; 
    	refresh(iTimes);
    }
    
    private void refresh(int time)
    {
       	dialog =  ProgressDialog.show(this, "loading", "loading the Nasa image of the day");
        new RetrieveFeedTask().execute(time);
    }
    
    private void resetDisplay(String title, String date, Bitmap image, String description)
    {
    	TextView titleView = (TextView ) findViewById(R.id.imageTitle);
    	titleView.setText(title);
    	
    	TextView dateView = (TextView ) findViewById(R.id.iamgeDate);
    	dateView.setText(date);
    	
    	ImageView imageView = (ImageView) findViewById(R.id.imageView1);
    	imageView.setImageBitmap(image);
    	
    	TextView descriptView = (TextView) findViewById(R.id.iamgeDescription);
    	descriptView.setText(description);

    }
    
    class RetrieveFeedTask extends AsyncTask<Integer, Void, IotdHandler> {
    	

        private Exception exception;
        @Override
        protected IotdHandler doInBackground(Integer...params) {
            try {
                
                SAXParserFactory factory =SAXParserFactory.newInstance();
                SAXParser parser=factory.newSAXParser();
                XMLReader xmlreader=parser.getXMLReader();
                IotdHandler theRSSHandler=new IotdHandler();
                theRSSHandler.setTimes(params[0]);
                xmlreader.setContentHandler(theRSSHandler);
                URL url= new URL(theRSSHandler.url);
                InputSource is=new InputSource(url.openStream());
                xmlreader.parse(is);
                return theRSSHandler;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(IotdHandler handler) {
            // TODO: check this.exception 
            // TODO: do something with the feed
            resetDisplay(handler.getTitle(), handler.getDate(), handler.getImage(), handler.getDescription().toString());
            if(dialog != null)
            	dialog.dismiss();

            
        }

    }  
    
    
}
