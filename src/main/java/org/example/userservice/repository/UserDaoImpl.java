package org.example.userservice.repository;

import org.example.userservice.config.HibernateConfig;
import org.example.userservice.exception.DatabaseException;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();
            logger.info("Пользователь успешно сохранен: {}", user.getEmail());
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при сохранении пользователя", e);
            throw new DatabaseException("Не удалось сохранить пользователя", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Session session = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            User user = session.get(User.class, id);
            logger.debug("Поиск пользователя по ID: {}", id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по ID: {}", id, e);
            throw new DatabaseException("Не удалось найти пользователя", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<User> findAll() {
        Session session = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            List<User> users = session.createQuery("FROM User", User.class).list();
            logger.debug("Получено всех пользователей: {}", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей", e);
            throw new DatabaseException("Не удалось получить список пользователей", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public User update(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            User existingUser = session.get(User.class, user.getId());
            if (existingUser == null) {
                throw new UserNotFoundException(user.getId());
            }

            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAge(user.getAge());

            session.merge(existingUser);

            transaction.commit();
            logger.info("Пользователь успешно обновлен: {}", user.getEmail());
            return existingUser;
        } catch (UserNotFoundException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при обновлении пользователя", e);
            throw new DatabaseException("Не удалось обновить пользователя", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(Long id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);
            if (user == null) {
                throw new UserNotFoundException(id);
            }

            session.delete(user);

            transaction.commit();
            logger.info("Пользователь успешно удален: {}", id);
        } catch (UserNotFoundException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при удалении пользователя", e);
            throw new DatabaseException("Не удалось удалить пользователя", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
