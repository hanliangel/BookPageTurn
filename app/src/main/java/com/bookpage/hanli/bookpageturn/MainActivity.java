package com.bookpage.hanli.bookpageturn;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.reader.hanli.readwidget.MimicPageTurnView;
import com.reader.hanli.readwidget.TextPageAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MimicPageTurnView turnView = new MimicPageTurnView(this);
        turnView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup)findViewById(android.R.id.content)).addView(turnView);
        TextPageAdapter adapter = new TextPageAdapter(this);
        adapter.setText("下面一片喝彩声，与他的体型相比，林君宸略显单薄，好似一个大怪和一个少年的比试，大家都比较看好史大奈。来者是一名皮肤黝黑的蓝衣壮汉，络腮胡子朝上下左右四个方向生长眼睛瞪得溜圆，手持双锤身手敏捷的很来去若奔雷之势。终于念到五号开战了，蓝袍小师哥吴梦达早早的就抢了一个战鼓，安静的守在旁边，只等林君宸开战就狠狠的擂鼓。凌霄山的斗站台像插入峡谷深渊的一把巨伞，伞面上凭空生出一条小路直冲凌霄正殿，好在伞面够大，本来师叔祖是想在此处建一个藏经阁楼，后来发现此地的整个风水与气势都比较适合做打斗场，斗站台就这么诞生了。为了不让凌霄弟子在打斗中受到无谓的伤害，师叔祖让每个比赛的弟子都身着凌霄特制的铠甲，每个甲片上面不是防御的功能是用牛皮注入猪血做成的液体铠甲用来模拟真人受伤效果，不仅有简单的防御功能，而且晋升的结果也是通过看铠甲上流的血和牛皮上伤口的位置是否致命来决定。");
        turnView.setPageTurnAdapter(adapter);
    }
}
