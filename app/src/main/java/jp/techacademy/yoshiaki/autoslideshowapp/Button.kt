package jp.techacademy.yoshiaki.autoslideshowapp

class Button(count: Int) {
    fun prebutton(count: Int,maxcount:Int): Int {
        var count:Int=count
        if (count >= 1) {
            count = count - 1
        } else {
            count = maxcount
        }
        return count
    }

    fun nextbutton(count: Int,maxcount:Int):Int {
        var count:Int=count
        if (maxcount == 0 || maxcount > count) {
            count = count + 1
        } else {
            count = 0
        }
        return count
    }

}