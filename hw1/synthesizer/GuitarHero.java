package synthesizer;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static void main(String[] args) {
        String keyboard="q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[]strings=new GuitarString[37];
        for (int i=0;i<strings.length;i++){
            strings[i]=new GuitarString(440.0*Math.pow(2.0,(i-24)/12));
        }

        while (true){
            int index=0;
            if (StdDraw.hasNextKeyTyped()){
                char key=StdDraw.nextKeyTyped();
                index=keyboard.indexOf(key);
                System.out.println(index);
                if (index==-1)
                    continue;
                strings[keyboard.indexOf(key)].pluck();
            }
            double sample=strings[index].sample()+strings[(index+10)%37].sample();
            StdAudio.play(sample);
            strings[index].tic();
            strings[(index+10)%37].tic();
        }
    }
}
