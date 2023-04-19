import {createSlice} from "@reduxjs/toolkit";

const infoSlice = createSlice({
    name: "INFO",
    initialState: {
        messages: []
    },
    reducers: {
        addMessage(state, action) {
            state.messages = [action.payload, ...state.messages];
        },
        removeMessage(state, action) {
            state.messages = state.messages
                .filter(msg => msg.id !== action.payload);
        }
    }
})

export default infoSlice.reducer
export const {addMessage, removeMessage} = infoSlice.actions


