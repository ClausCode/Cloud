import styles from './index.module.scss'

const Error = ({children}) => {
    return <div className={styles.alert}>
        <h3>!</h3> {children}
    </div>
}

export default Error;