import java.util.HashMap;
import java.util.List;

public class AFD {
    private String[] tree_info;
    private List<Nodo> functions_table;
    private HashMap[] transitions_table;

    public AFD() {
        tree_info = new String[];
        functions_table = new HashMap;
        transitions_table = new HashMap;
    }

    public String[] getTree_info() {
        return tree_info;
    }

    public void setTree_info(String[] tree_info) {
        this.tree_info = tree_info;
    }

    public List<Nodo> getFunctions_table() {
        return functions_table;
    }

    public void setFunctions_table(List<Nodo> functions_table) {
        this.functions_table = functions_table;
    }

    public HashMap[] getTransitions_table() {
        return transitions_table;
    }

    public void setTransitions_table(HashMap[] transitions_table) {
        this.transitions_table = transitions_table;
    }

    public static boolean isAlphanumeric(String str) {
        return str.matches("[a-zA-Z0-9]+");
    }

    private void read_tree(String[] tree) {
        // Depends on what is received
        tree_info = ["a", "b", "|", "*", "a", ".", "b", ".", "b", ".", "#", "."]
    }

    private void check_calculated_functions() {
        for (int i = 0; i < tree_info.length(); i++) {
            if (isAlphanumeric(value)) {

            }
        }

    }

    private void create_transitions() {

    }

    public static void main(String[] args) {
        String[] try_tree = [., [., [., [., [*, [|, [a, b]], a], b], b], #]];
        read_tree(try_tree);

        check_calculated_functions();
    }
}
