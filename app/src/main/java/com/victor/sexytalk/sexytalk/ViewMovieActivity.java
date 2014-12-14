package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;


public class ViewMovieActivity extends Activity {
    TextView loveMessage;
    ImageView playVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie);
        loveMessage = (TextView) findViewById(R.id.loveMessage);
        playVideo = (ImageView) findViewById(R.id.playVideo);
        final String videoUrl = getIntent().getStringExtra(Statics.KEY_URL);
        //final Uri videoUri = getIntent().getData(); //vzima Uri deto go podadohme ot drugata strana
        String loveMessageToDisplay = getIntent().getStringExtra(Statics.KEY_LOVE_MESSAGE);
        loveMessage.setText(loveMessageToDisplay);


        Uri uri=Uri.parse(videoUrl);

        VideoView video=(VideoView)findViewById(R.id.videoView);
        video.setVideoURI(uri);
        video.start();
       playVideo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Uri myUri = Uri.parse(videoUrl);
               Intent intent = new Intent(Intent.ACTION_VIEW, myUri);
               intent.setDataAndType(myUri,"video/*");
               startActivity(intent);
           }
       });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
