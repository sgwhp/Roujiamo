#Roujiamo
------
<img src="https://github.com/sgwhp/Roujiamo/blob/master/screenshot/screenshot.gif" />

At the moment it provides:
* ``Burger``
Burger is an implementation of a design from Dribbble [Open & Close][1] by Creativedash.
* ``Dipper``
Dipper is an implementation of a design from Dribbble [On & Off][2] by Creativedash.
* ``Material Burger``
Material Burger is an implementation of a design from [Google Material Design][5].
* ``Sandwich``
Sandwich is an implementation of a design from [Play & Pause][4].

## Usage
  example:
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:roujiamo="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <cn.robust.roujiamo.library.view.Roujiamo
        android:id="@+id/burger"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/green"
        roujiamo:drawable="Burger" />

</RelativeLayout>
```

  For more details, just check out the sample project.

## TODO
* [Shut up][3]


  [1]: https://dribbble.com/shots/1623679-Open-Close?list=shots&sort=popular&timeframe=year&offset=0
  [2]: https://dribbble.com/shots/1631598-On-Off?list=shots&sort=popular&timeframe=year&offset=34
  [3]: https://dribbble.com/shots/1660442-Shut-Up?list=shots&sort=popular&timeframe=year&offset=52
  [4]: https://dribbble.com/shots/1681359-Play-Pause?list=users&offset=52
  [5]: https://dribbble.com/shots/1621920-Google-Material-Design-Free-AE-Project-File?list=shots&sort=popular&timeframe=year&offset=19