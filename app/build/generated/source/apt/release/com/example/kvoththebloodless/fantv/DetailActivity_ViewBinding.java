// Generated code from Butter Knife. Do not modify!
package com.example.kvoththebloodless.fantv;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DetailActivity_ViewBinding<T extends DetailActivity> implements Unbinder {
  protected T target;

  @UiThread
  public DetailActivity_ViewBinding(T target, View source) {
    this.target = target;

    target.Rating = Utils.findRequiredViewAsType(source, R.id.rating1, "field 'Rating'", TextView.class);
    target.Airdate = Utils.findRequiredViewAsType(source, R.id.airdate, "field 'Airdate'", TextView.class);
    target.Runtime = Utils.findRequiredViewAsType(source, R.id.runtime, "field 'Runtime'", TextView.class);
    target.Lang = Utils.findRequiredViewAsType(source, R.id.lang, "field 'Lang'", TextView.class);
    target.plot = Utils.findRequiredViewAsType(source, R.id.plot, "field 'plot'", TextView.class);
    target.Poster = Utils.findRequiredViewAsType(source, R.id.overlay_poster, "field 'Poster'", DynamicHeightNetworkImageView.class);
    target.still = Utils.findRequiredViewAsType(source, R.id.detail_still, "field 'still'", DynamicHeightNetworkImageView.class);
    target.Epname = Utils.findRequiredViewAsType(source, R.id.Epname, "field 'Epname'", TextView.class);
    target.next = Utils.findRequiredViewAsType(source, R.id.nextep, "field 'next'", ImageButton.class);
    target.prev = Utils.findRequiredViewAsType(source, R.id.prevep, "field 'prev'", ImageButton.class);
    target.banner = Utils.findRequiredViewAsType(source, R.id.bleedpic, "field 'banner'", DynamicHeightNetworkImageView.class);
    target.view = Utils.findRequiredView(source, R.id.calendarview, "field 'view'");
    target.Readmore = Utils.findRequiredViewAsType(source, R.id.readmore, "field 'Readmore'", Button.class);
    target.watched = Utils.findRequiredViewAsType(source, R.id.watched, "field 'watched'", CheckBox.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.Rating = null;
    target.Airdate = null;
    target.Runtime = null;
    target.Lang = null;
    target.plot = null;
    target.Poster = null;
    target.still = null;
    target.Epname = null;
    target.next = null;
    target.prev = null;
    target.banner = null;
    target.view = null;
    target.Readmore = null;
    target.watched = null;

    this.target = null;
  }
}
