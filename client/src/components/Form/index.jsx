import styles from './index.module.scss'

import Logo from '/src/assets/logo.svg'

const Form = ({title, handle, children}) => {
    return (
        <div className={styles.cloud__form}>
            <div className={styles.form_header}>
                <img className={styles.picture} src={Logo} alt="Logo"/>
                <h1 className={styles.title}>
                    {title}
                </h1>
            </div>
            <form onSubmit={handle} className={styles.form_body}>
                {children}
            </form>
        </div>
    )
}

export default Form;