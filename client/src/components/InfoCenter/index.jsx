import styles from "./index.module.scss"

import Close from "/src/assets/icons/close.svg"
import {useDispatch, useSelector} from "react-redux";
import {removeMessage} from "../../api/store/slice/infoSlice.js";

const InfoCenter = () => {
    const messages = useSelector(state => state.info.messages)
    const dispatch = useDispatch()

    return <div className={styles.info_box}>
        {
            messages.map(msg =>
                <div key={msg.id} className={[styles.popup, styles[msg.style]].join(' ')}>
                    <p>{msg.text}</p>
                    <img onClick={() => dispatch(removeMessage(msg.id))} src={Close} alt="Close"/>
                </div>)
        }
    </div>
}

export default InfoCenter