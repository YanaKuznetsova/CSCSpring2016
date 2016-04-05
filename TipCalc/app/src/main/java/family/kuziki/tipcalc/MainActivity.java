package family.kuziki.tipcalc;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String BILL_TOTAL = "BILL_TOTAL";
    private static final String CUSTOM_PERCENT = "CUSTOM_PERCENT";

    private double currentBillTotal;
    private int currentCustomPercent;
    private EditText tip10EditText;
    private EditText total10editText;
    private EditText tip15EditText;
    private EditText total15editText;
    private EditText tip20EditText;
    private EditText total20editText;
    private EditText billEditText;
    private TextView customTipTextView;
    private EditText tipCustomEditText;
    private EditText totalCustomEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            currentBillTotal = 0.0;
            currentCustomPercent = 18;
        } else {
            currentBillTotal = savedInstanceState.getDouble(BILL_TOTAL);
            currentCustomPercent = savedInstanceState.getInt(CUSTOM_PERCENT);
        }

        tip10EditText = (EditText) findViewById(R.id.tip10EditText);
        total10editText = (EditText) findViewById(R.id.total10editText);
        tip15EditText = (EditText) findViewById(R.id.tip15EditText);
        total15editText = (EditText) findViewById(R.id.total15editText);
        tip20EditText = (EditText) findViewById(R.id.tip20EditText);
        total20editText = (EditText) findViewById(R.id.total20editText);

        customTipTextView = (TextView) findViewById(R.id.customTextView);
        tipCustomEditText = (EditText) findViewById(R.id.tipCustomEditText);
        totalCustomEditText = (EditText) findViewById(R.id.totalCustomEditText);
        billEditText = (EditText) findViewById(R.id.billEditText);

        billEditText.addTextChangedListener(billEditTextWatcher);
        SeekBar customSeekBar = (SeekBar) findViewById(R.id.customSeekBar);
        customSeekBar.setOnSeekBarChangeListener(customSeekBarListener);

        updateCustom();
        updateStandard();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private SeekBar.OnSeekBarChangeListener customSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentCustomPercent = seekBar.getProgress();
            updateCustom();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private TextWatcher billEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            try {
                currentBillTotal = Double.parseDouble(s.toString());
            } catch (NumberFormatException e) {
                currentBillTotal = 0.0;
            }
            updateStandard();
            updateCustom();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateStandard(){
        double tenPercentTip = currentBillTotal*0.1;
        double tenPercentTotal = currentBillTotal + tenPercentTip;
        tip10EditText.setText(String.format(" %.02f", tenPercentTip));
        total10editText.setText(String.format(" %.02f", tenPercentTotal));

        double fifteenPercentTip = currentBillTotal*0.15;
        double fifteenPercentTotal = currentBillTotal + fifteenPercentTip;
        tip15EditText.setText(String.format(" %.02f", fifteenPercentTip));
        total15editText.setText(String.format(" %.02f", fifteenPercentTotal));

        double twentyPercentTip = currentBillTotal*0.2;
        double twentyPercentTotal = currentBillTotal + twentyPercentTip;
        tip20EditText.setText(String.format(" %.02f", twentyPercentTip));
        total20editText.setText(String.format(" %.02f", twentyPercentTotal));
    }

    private void updateCustom (){
        customTipTextView.setText(currentCustomPercent + " %");
        double customTipAmount = currentBillTotal * currentCustomPercent * 0.01;
        double customTotalAmount = currentBillTotal + customTipAmount;
        tipCustomEditText.setText(String.format(" %.02f", customTipAmount));
        totalCustomEditText.setText(String.format(" %.02f", customTotalAmount));
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble(BILL_TOTAL, currentBillTotal);
        outState.putInt(CUSTOM_PERCENT, currentCustomPercent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
