package br.com.thiengo.chatpage;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.LinkedList;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    private LinkedList<SpannableStringBuilder> messages;
    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private EditText etMessage;
    private MaterialDialog materialDialog;
    private boolean isDialogActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMessage = (EditText) findViewById(R.id.et_message);
        etMessage.addTextChangedListener(this);
        messages = new LinkedList<>();

        initRecycler();
        initDialog();
    }

    private void initRecycler(){
        rvMessages = (RecyclerView) findViewById(R.id.rv_chat);
        rvMessages.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd( true );
        rvMessages.setLayoutManager( layoutManager );

        adapter = new MessagesAdapter( this, messages );
        rvMessages.setAdapter( adapter );
    }

    private void initDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        FlexboxLayout fl = (FlexboxLayout) inflater.inflate(R.layout.emoticons, null);
        materialDialog = new MaterialDialog(this);

        materialDialog.setView(fl);
        materialDialog.setCanceledOnTouchOutside(true);
    }

    public void newMessage( View view ){
        /* PADRÃO CLÁUSULA DE GUARDA PARA EVITAR MENSAGENS
         * VAZIAS
         * */
        if( etMessage.getText().length() == 0 ){
            return;
        }

        SpannableStringBuilder message = (SpannableStringBuilder) etMessage.getText();

        etMessage.setText("");
        messages.add( message );
        adapter.notifyDataSetChanged();
        rvMessages.getLayoutManager().scrollToPosition( messages.size() - 1 );
    }

    public void chooseEmoticon( View view ){
        materialDialog.show();
        isDialogActivated = true;
    }

    public void chosenEmoticon(View view){
        SpannableStringBuilder message = (SpannableStringBuilder) etMessage.getText();

        /* OBTENDO O TEXTO ATUAL EM DUAS PARTES, A PRIMEIRA
         * É ANTES DO CURSOR, A SEGUNDA É DEPOIS DELE
         * (INCLUINDO ELE), ASSIM SERÁ POSSÍVEL COLOCAR O
         * EMOTICON EM QUALQUER PARTE DA STRING E NÃO SOMENTE
         * NO FINAL
         * */
        SpannableStringBuilder m1 = getSubSetMessage( 0, etMessage.getSelectionStart() );
        SpannableStringBuilder m2 = getSubSetMessage( etMessage.getSelectionStart(), message.length() );

        ImageView iv = (ImageView) view;
        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();

        /* DIMINUINDO O TAMANHO DO ÍCONE PARA COLOCA-LO NA
         * MENSAGEM
         * */
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            getDpToPx( 34 ),
            getDpToPx( 34 ),
            false );

        ImageSpan is = new ImageSpan( this, bitmap, ImageSpan.ALIGN_BASELINE );

        /* EXATAMENTE NO FINAL DA PRIMEIRA METADA DO TEXTO É
         * QUE VAMOS ADICIONAR O EMOTICON, DEPOIS DE
         * ADICIONARMOS UM ESPAÇÕES EM BRANCO, O QUE VAI SER
         * SUBSTITUÍDO PELO EMOTICON
         * */
        m1.append(" ");
        m1.setSpan( is, m1.length() - 1, m1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

        message.clear();
        message.append( m1 );
        message.append( m2 );

        /* PARA COLOCAR O CURSOR EXATAMENTE ONDE ESTAVA
         * ANTES DA ADIÇÃO DO ÍCONE
         * */
        etMessage.setSelection( m1.length() );

        /* DEVIDO A ADIÇÃO DO ÍCONE TAMBÉM VIA PADRÃO EM TEXTO,
         * ONDE NÃO HÁ ABERTURA DE DIALOG, DEVEMOS UTILIZAR UM
         * FLAG PARA SABER SE O DIALOG ESTÁ ABERTA PARA ENTÃO
         * INVOCAR O dismiss(), CASO CONTRÁRIO HAVERÁ UMA EXCEPTION
         * */
        if( isDialogActivated ){
            materialDialog.dismiss();
            isDialogActivated = false;
        }
    }

    public static int getDpToPx(int pixels ){
        return (int) (pixels * Resources.getSystem().getDisplayMetrics().density);
    }


    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if( charSequence.length() > 1 ){
            ImageView iv = new ImageView(this);
            int lastPos = etMessage.getSelectionStart() - 1;
            int icon;

            /* PADRÃO CLÁUSULA DE GUARDA PARA NÃO TER PROCESAMENTO
             * QUANDO AS CONDIÇÕES MÍNIMAS NÃO FOREM ATENDIDAS
             * */
            if( lastPos < 0
                || charSequence.charAt(lastPos - 1) != ':'
                || (charSequence.charAt(lastPos) != ')'
                    && charSequence.charAt(lastPos) != '(') ){
                return;
            }

            if( charSequence.charAt(lastPos) == ')' ){
                icon = R.drawable.ic_emoticon_laughing;
            }
            else {
                icon = R.drawable.ic_emoticon_sad;
            }

            SpannableStringBuilder message = (SpannableStringBuilder) etMessage.getText();

            /* REMOVENDO O PADRÃO ":)" OU ":(" DA STRING
             * PARA COLOCAR O EMOTICON
             * */
            SpannableStringBuilder m1 = getSubSetMessage( 0, lastPos - 1 );
            SpannableStringBuilder m2 = getSubSetMessage( lastPos + 1, message.length() );

            message.clear();
            message.append( m1 );
            message.append( m2 );

            /* PARA COLOCAR O CURSOR EXATAMENTE ONDE ESTAVA
             * ANTES DA ADIÇÃO DO ÍCONE
             * */
            etMessage.setSelection( m1.length() );

            iv.setImageResource( icon );
            chosenEmoticon( iv );
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {}

    private SpannableStringBuilder getSubSetMessage( int start, int end ){
        SpannableStringBuilder m = (SpannableStringBuilder) etMessage.getText();
        m = (SpannableStringBuilder) m.subSequence( start, end );
        return m;
    }
}
