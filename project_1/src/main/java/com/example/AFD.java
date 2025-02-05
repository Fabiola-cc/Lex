import java.util.HashMap;

public class AFD {
    private tree_info String[];
    private functions_table HashMap[];
    private transitions_table HashMap[];

    AFD() {
        tree_info = new String[];
        functions_table = new HashMap;
        transitions_table = new HashMap;
    }

    public tree_info[] getString() {
        return String;
    }

    public void setString(tree_info[] string) {
        String = string;
    }

    public functions_table[] getHashMap() {
        return HashMap;
    }

    public void setHashMap(functions_table[] hashMap) {
        HashMap = hashMap;
    }

    public functions_table[] getHashMap() {
        return HashMap;
    }

    public void setHashMap(functions_table[] hashMap) {
        HashMap = hashMap;
    }

    public static boolean esAlfanumerico(String str) {
        return str.matches("[a-zA-Z0-9]+");
    }

    private void read_tree(String[] tree) {
        // Depends on what is received
        tree_info = ["a", "b", "|", "*", "a", ".", "b", ".", "b", ".", "#", "."]
    }

    private void check_calculated_functions() {
        for (int i = 0; i < tree_info.length(); i++) {
            if (esAlfanumerico(value)) {

            }
        }

    }

    private void create_transitions() {

    }

    public void main(String args[]) {
        String[] try_tree = [., [., [., [., [*, [|, [a, b]], a], b], b], #]];
        read_tree(try_tree);

        check_calculated_functions();
    }
}
