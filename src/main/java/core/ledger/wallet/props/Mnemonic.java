package core.ledger.wallet.props;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Mnemonic {

    public static ArrayList<String> mnemonicPhrases = new ArrayList<>();

    public static void initPhrases(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "data/master/config/mnemonic.txt"));
            String line = reader.readLine();
            while (line != null) {
                mnemonicPhrases.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static @NotNull String @NotNull [] buildMnemonicPassword(){
        String[] mnemonic = new String[12];
        Random rand = new Random();
        for(int i = 0; i < mnemonic.length; i++){
            mnemonic[i] = mnemonicPhrases.get(rand.nextInt(mnemonicPhrases.size() - 1));
        }
        return mnemonic;
    }
}
