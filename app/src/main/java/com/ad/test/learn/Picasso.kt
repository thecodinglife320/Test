package com.ad.test.learn

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ad.test.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation

class PicassoAc : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.picasso_view)

        val logoUrl =
            "https://resources.jetbrains.com/storage/products/intellij-idea/img/meta/intellij-idea_logo_300x300.png"

        Picasso.get()
            .load(logoUrl)
            .fit()
//            .centerCrop()
            .transform(
                BlurTransformation(this)
            )
            .centerInside()
            .into(findViewById<ImageView>(R.id.image))
    }
}