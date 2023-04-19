import {addMessage} from "../store/slice/infoSlice.js";
import store from "../store/index.js";

export const showMessage = (text, style = 'info') => {
    store.dispatch(addMessage({
        id: new Date().getMilliseconds(),
        style: style,
        text: text
    }))
}