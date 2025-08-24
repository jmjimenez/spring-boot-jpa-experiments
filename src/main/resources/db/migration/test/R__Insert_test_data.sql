-- Repeatable migration for test profile
-- This migration will be executed every time the application starts with the test profile
-- The R__ prefix makes it a repeatable migration

-- Clear existing data to ensure fresh state
TRUNCATE TABLE post_tag CASCADE;

TRUNCATE TABLE user_tag CASCADE;

TRUNCATE TABLE tag CASCADE;

TRUNCATE TABLE Post CASCADE;

TRUNCATE TABLE Users CASCADE;

-- Reset the sequence to start from 1
ALTER SEQUENCE users_id_seq RESTART WITH 1;

ALTER SEQUENCE post_id_seq RESTART WITH 1;

ALTER SEQUENCE tag_id_seq RESTART WITH 1;

-- Insert Users
INSERT INTO
    Users (
        uuid,
        name,
        email,
        username,
        password
    )
VALUES (
        '550e8400-e29b-41d4-a716-446655440001',
        'Leanne Graham',
        'leanne.graham@example.com',
        'leanne_graham',
        '$2a$10$nJepjv6WEk5NlbiaU2ka3uR/5N7/wqtrw6/1acgPw7Rvr4qxoidR.'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440002',
        'Ervin Howell',
        'ervin.howell@example.com',
        'ervin_howell',
        '$2a$10$nJepjv6WEk5NlbiaU2ka3uR/5N7/wqtrw6/1acgPw7Rvr4qxoidR.'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440003',
        'Clementine Bauch',
        'clementine.bauch@example.com',
        'clementine_bauch',
        '$2a$10$nJepjv6WEk5NlbiaU2ka3uR/5N7/wqtrw6/1acgPw7Rvr4qxoidR.'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440004',
        'Patricia Lebsack',
        'patricia.lebsack@example.com',
        'patricia_lebsack',
        '$2a$10$nJepjv6WEk5NlbiaU2ka3uR/5N7/wqtrw6/1acgPw7Rvr4qxoidR.'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440005',
        'Chelsey Dietrich',
        'chelsey.dietrich@example.com',
        'chelsey_dietrich',
        '$2a$10$nJepjv6WEk5NlbiaU2ka3uR/5N7/wqtrw6/1acgPw7Rvr4qxoidR.'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440006',
        'Admin User',
        'admin@example.com',
        'admin',
        '$2a$10$nJepjv6WEk5NlbiaU2ka3uR/5N7/wqtrw6/1acgPw7Rvr4qxoidR.'
    );

-- Insert Tags
INSERT INTO
    tag (uuid, tag)
VALUES (
        '550e8400-e29b-41d4-a716-446655440056',
        'technology'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440057',
        'programming'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440058',
        'java'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440059',
        'spring-boot'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440060',
        'jpa'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440061',
        'database'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440062',
        'web-development'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440063',
        'tutorial'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440064',
        'best-practices'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440065',
        'architecture'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440066',
        'microservices'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440067',
        'testing'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440068',
        'devops'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440069',
        'api'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440070',
        'security'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440071',
        'developer'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440072',
        'not-used'
    );

-- Insert Posts for Leanne Graham (user_id = 1)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440006',
        1,
        'sunt aut facere repellat provident occaecati excepturi optio reprehenderit',
        'quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440007',
        1,
        'qui est esse',
        'est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440008',
        1,
        'ea molestias quasi exercitationem repellat qui ipsa sit aut',
        'et iusto sed quo iure\nvoluptatem occaecati omnis eligendi aut ad\nvoluptatem doloribus vel accusantium quis pariatur\nmolestiae porro eius odio et labore et velit aut'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440009',
        1,
        'eum et est occaecati',
        'ullam et saepe reiciendis voluptatem adipisci\nsit amet autem assumenda provident rerum culpa\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\nquis sunt voluptatem rerum illo velit'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440010',
        1,
        'nesciunt quas odio',
        'repudiandae veniam quaerat sunt sed\nalias aut fugiat sit autem sed est\nvoluptatem omnis possimus esse voluptatibus quis\nest aut tenetur dolor neque'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440011',
        1,
        'dolorem eum magni eos aperiam quia',
        'ut aspernatur corporis harum nihil quis provident sequi\nmollitia nobis aliquid molestiae\nperspiciatis et ea nemo ab reprehenderit accusantium quas\nvoluptate dolores velit et doloremque molestiae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440012',
        1,
        'magnam facilis autem',
        'dolore placeat quibusdam ea quo vitae\nmagni quis enim qui quis quo nemo aut saepe\nquidem repellat excepturi ut quia\nsunt ut sequi eos ea sed quas'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440013',
        1,
        'dolorem dolore est ipsam',
        'dignissimos aperiam dolorem qui eum\nfacilis quibusdam animi sint suscipit qui sint possimus cum\nquaerat magni maiores excepturi\nipsam ut commodi dolor voluptatum modi aut vitae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440014',
        1,
        'nesciunt iure omnis dolorem tempora et accusantium',
        'consectetur animi nesciunt iure dolore\nenim quia ad\nveniam autem ut quam aut nobis\net est aut quod aut provident voluptas autem voluptas'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440015',
        1,
        'optio molestias id quia eum',
        'quo et expedita modi cum officia vel magni\ndoloribus qui repudiandae\nvero nisi sit\nquos veniam quod sed accusamus veritatis error'
    );

-- Insert Posts for Ervin Howell (user_id = 2)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440016',
        2,
        'et ea vero quia laudantium autem',
        'delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\naccusamus in eum beatae sit\nvel qui neque voluptates ut commodi qui incidunt\nut animi commodi'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440017',
        2,
        'in quibusdam tempore odit est dolorem',
        'itaque id aut magnam\npraesentium quia et ea odit et ea voluptas et\nsapiente quia nihil amet occaecati quia id voluptatem\nincidunt ea est distinctio odio'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440018',
        2,
        'dolorum ut in voluptas mollitia et saepe quo animi',
        'aut dicta possimus sint mollitia voluptas commodi quo doloremque\niste corrupti reiciendis voluptatem eius rerum\nsit cumque quod eligendi laborum minima\nperferendis recusandae assumenda consectetur porro architecto ipsum ipsam'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440019',
        2,
        'voluptatem eligendi optio',
        'fuga et accusamus dolorum perferendis illo voluptas\nnon doloremque neque facere\nad qui dolorum molestiae beatae\nsed aut voluptas totam sit illum'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440020',
        2,
        'eveniet quod temporibus',
        'reprehenderit quos placeat\nvelit minima officia dolores impedit repudiandae molestiae nam\nvoluptas recusandae quis delectus\nofficiis harum fugiat vitae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440021',
        2,
        'sint suscipit perspiciatis velit dolorum rerum ipsa laboriosam odio',
        'suscipit nam nisi quo aperiam aut\nasperiores eos fugit maiores voluptatibus quia\nvoluptatem quis ullam qui in alias quia est\nconsequatur magni mollitia accusamus ea nisi voluptate dicta'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440022',
        2,
        'fugit voluptas sed molestias voluptatem provident',
        'eos voluptas et aut odit natus earum\naspernatur fuga molestiae ullam\ndeserunt ratione qui eos\nqui nihil ratione nemo velit ut aut id quo'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440023',
        2,
        'voluptate et itaque vero tempora molestiae',
        'eveniet quo quis\nlaborum totam consequatur non dolor\nut et est repudiandae\nest voluptatem vel debitis et magnam'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440024',
        2,
        'adipisci placeat illum aut reiciendis qui',
        'illum quis cupiditate provident sit magnam\nea sed aut omnis\nveniam maiores ullam consequatur atque\nadipisci quo iste expedita sit quos voluptas'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440025',
        2,
        'doloribus ad provident suscipit at',
        'qui consequuntur ducimus possimus quisquam amet similique\nsuscipit porro ipsam amet\neos veritatis officiis exercitationem vel fugit aut necessitatibus totam\nomnis rerum consequatur expedita quidem cumque explicabo'
    );

-- Insert Posts for Clementine Bauch (user_id = 3)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440026',
        3,
        'asperiores ea ipsam voluptatibus modi minima quia sint',
        'repellat aliquid praesentium dolorem quo\nsed totam minus non itaque\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\ntempora et tenetur expedita sunt'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440027',
        3,
        'dolor sint quo a velit explicabo quia nam',
        'eos qui et ipsum ipsam suscipit aut\nsed omnis non odio\nexpedita earum mollitia molestiae aut atque rem suscipit\nnam impedit esse'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440028',
        3,
        'maxime id vitae nihil numquam',
        'veritatis unde neque eligendi\nquae quod architecto quo neque vitae\nest illo sit tempora doloremque fugit quod\net et vel beatae sequi ullam sed tenetur perspiciatis'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440029',
        3,
        'autem hic labore sunt dolores incidunt',
        'enim et ex nulla\nomnis voluptas quia qui\nvoluptatem consequatur numquam aliquam sunt\ntotam recusandae id dignissimos aut sed asperiores deserunt'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440030',
        3,
        'rem alias distinctio quo quis',
        'ullam consequatur ut\nomnis quis sit vel consequuntur\nipsa eligendi ipsum molestiae et omnis error nostrum\nmolestiae illo tempore quia et distinctio'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440031',
        3,
        'est et quae odit qui non',
        'similique esse doloribus nihil accusamus\nomnis dolorem fuga consequuntur reprehenderit fugit recusandae temporibus\nperspiciatis cum ut laudantium\nomnis aut molestiae vel vero'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440032',
        3,
        'quasi id et eos tenetur aut quo autem',
        'eum sed dolores ipsam sint possimus debitis occaecati\ndebitis qui qui et\nut placeat enim earum aut odit facilis\nconsequatur suscipit necessitatibus rerum sed inventore temporibus consequatur'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440033',
        3,
        'delectus ullam et corporis nulla voluptas sequi',
        'non et quaerat ex quae ad maiores\nmaiores recusandae totam aut blanditiis mollitia quas illo\nut voluptatibus voluptatem\nsimilique nostrum eum'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440034',
        3,
        'iusto eius quod necessitatibus culpa ea',
        'odit magnam ut saepe sed non qui\ntempora atque nihil\naccusamus illum doloribus illo dolor\neligendi repudiandae odit magni similique sed cum maiores'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440035',
        3,
        'a quo magni similique perferendis',
        'alias dolor cumque\nimpedit blanditiis non eveniet odio maxime\nblanditiis amet eius quis tempora quia autem rem\na provident perspiciatis quia'
    );

-- Insert Posts for Patricia Lebsack (user_id = 4)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440036',
        4,
        'ullam ut quidem id aut vel consequuntur',
        'debitis eius sed quibusdam non quis consectetur vitae\nimpedit ut qui consequatur sed aut in\nquidem sit nostrum et maiores adipisci atque\nquaerat voluptatem adipisci repudiandae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440037',
        4,
        'doloremque illum aliquid sunt',
        'deserunt eos nobis asperiores et hic\nest debitis repellat molestiae optio\nnihil ratione ut eos beatae quibusdam distinctio maiores\nearum voluptates et aut adipisci ea maiores voluptas maxime'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440038',
        4,
        'qui explicabo molestiae dolorem',
        'rerum ut et numquam laborum odit est sit\nid qui sint in\nquasi tenetur tempore aperiam et quaerat qui in\nut omnis aut occaecati molestiae nemo repellendus pariatur deserunt vero'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440039',
        4,
        'magnam ut rerum iure',
        'ea velit perferendis earum ut voluptatem voluptatibus itaque\net ea aut magni omnis a\nut voluptatem voluptatibus assumenda sunt nobis tempora atque\nnon amet error deserunt laborum'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440040',
        4,
        'id nihil consequatur molestias animi provident',
        'nisi error delectus possimus ut eligendi vitae\nplaceat totam aut veritatis deserunt est\narchitecto est quo qui veritatis\nad assumenda facilis quae omnis qui qui illum'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440041',
        4,
        'fuga nam accusamus voluptas reiciendis itaque',
        'ad mollitia et omnis minus architecto delectus\nsaepe dolorem animi tempore molestias\nomnis perspiciatis repellat placeat eos\nrem quia aut ab corporis aut'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440042',
        4,
        'provident vel ut sit nesciunt',
        'debitis aut quis non possimus ex officiis\nquisquam facilis omnis occaecati alias aut fuga tempora\ndo necessitatibus sit a quibusdam a\nsaepe aut ullam assumenda excepturi repellat neque vel'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440043',
        4,
        'laudantium enim quasi est quidem magnam voluptate ipsam eos',
        'tempora quo necessitatibus\ndolorum aut ut quos asperiores\nmolestiae veritatis sequi\nsint voluptatem iste perferendis est'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440044',
        4,
        'soluta aliquam aperiam consequatur illo quis voluptas',
        'sunt dolores aut dolorem\nexplicabo facilis harum nam eos similique\nomnis voluptas ea itaque\nblanditiis nulla ab et magnam'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440045',
        4,
        'qui enim et consequuntur quia animi quis voluptate quibusdam',
        'iusto est quibusdam fuga quas quaerat molestias\na enim ut assumenda dolores veniam tenetur\niste aut autem textus quibusdam cupiditate\net possimus sunt qui veritatis'
    );

-- Insert Posts for Chelsey Dietrich (user_id = 5)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440046',
        5,
        'ut quo aut ducimus alias',
        'minima harum praesentium eum rerum illo dolore\nquasi exercitationem rerum nam\nporro quis neque quo\nconsequatur minus dolor quidem veritatis sunt non explicabo similique'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440047',
        5,
        'sit asperiores ipsam eveniet odio non quia',
        'totam corporis dignissimos\nvitae dolorem ut occaecati accusamus\nex velit deserunt\net exercitationem vero incidunt corrupti mollitia'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440048',
        5,
        'sit vel voluptatem et non libero',
        'debitis excepturi ea perferendis harum libero optio\neos accusamus cum fuga ut sapiente repudiandae\net ut incidunt omnis molestiae\nnihil ut eum odit'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440049',
        5,
        'qui et at rerum necessitatibus',
        'aut est omnis dolores\nneque rerum quod ea rerum velit pariatur beatae non\ncum tenetur omnis rerum tempora\nnemo enim doloremque quia voluptas facere sed qui'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440050',
        5,
        'sed ab est est',
        'at pariatur consequuntur earum quidem\nquo est laudantium soluta voluptatem\nqui ullam et est\net cum voluptas voluptatum repellat est'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440051',
        5,
        'voluptatum itaque dolores nisi et quasi',
        'tenetur explicabo eius illum qui cumque\nvoluptas hic non qui\ndebitis ut est facilis alias quis facilis\net iusto velit iste et'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440052',
        5,
        'qui commodi dolor at maiores et quis id accusantium',
        'perspiciatis consequatur qui in porro et\nquibusdam porro in esse similique fuga\nipsa eum sit occaecati iure aliquam quia vel\nadipisci ab iste vel hic ut et commodi'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440053',
        5,
        'consequatur placeat omnis quisquam quia reprehenderit fugit veritatis facilis',
        'repudiandae et numquam perferendis sed alias ut\nqui omnis aut\nvoluptatem et natus esse sapiente at\nut dolores aspernatur non'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440054',
        5,
        'voluptatem nulla commodi expedita similique non',
        'sunt repudiandae sed aut aperiam\nlaudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolorum aut ut quos asperiores'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440055',
        5,
        'rem veniam quaerat et nemo molestiae excepturi',
        'aut sunt sed neque labore quaerat soluta temporibus\ncorporis sed in consequatur eos repellat\ninventore tenetur aut voluptatem vel\ntotam asperiores autem et quas sed ipsa'
    );

-- Insert User Tags
-- Leanne Graham (user_id = 1) - Technology enthusiast
INSERT INTO
    user_tag (user_id, tag_id)
VALUES (1, 1), -- technology
    (1, 2), -- programming
    (1, 3), -- java
    (1, 4), -- spring-boot
    (1, 16);
-- developer

-- Ervin Howell (user_id = 2) - Database expert
INSERT INTO
    user_tag (user_id, tag_id)
VALUES (2, 1), -- technology
    (2, 6), -- database
    (2, 5), -- jpa
    (2, 7);
-- web-development

-- Clementine Bauch (user_id = 3) - Tutorial creator
INSERT INTO
    user_tag (user_id, tag_id)
VALUES (3, 1), -- technology
    (3, 8), -- tutorial
    (3, 9), -- best-practices
    (3, 10);
-- architecture

-- Patricia Lebsack (user_id = 4) - DevOps specialist
INSERT INTO
    user_tag (user_id, tag_id)
VALUES (4, 1), -- technology
    (4, 13), -- devops
    (4, 12), -- testing
    (4, 11);
-- microservices

-- Chelsey Dietrich (user_id = 5) - Security expert
INSERT INTO
    user_tag (user_id, tag_id)
VALUES (5, 1), -- technology
    (5, 15), -- security
    (5, 14), -- api
    (5, 9);
-- best-practices

-- Insert Post Tags
-- Technology posts (posts 1-5) - Leanne Graham
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (1, 1),
    (1, 2),
    (1, 3), -- technology, programming, java
    (2, 1),
    (2, 4),
    (2, 5), -- technology, spring-boot, jpa
    (3, 1),
    (3, 6),
    (3, 7), -- technology, database, web-development
    (4, 1),
    (4, 8),
    (4, 9), -- technology, tutorial, best-practices
    (5, 1),
    (5, 10),
    (5, 11);
-- technology, architecture, microservices

-- Database posts (posts 6-10) - Leanne Graham
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (6, 6),
    (6, 5),
    (6, 9), -- database, jpa, best-practices
    (7, 6),
    (7, 7),
    (7, 12), -- database, web-development, testing
    (8, 6),
    (8, 10),
    (8, 13), -- database, architecture, devops
    (9, 6),
    (9, 14),
    (9, 15), -- database, api, security
    (10, 6),
    (10, 8),
    (10, 9);
-- database, tutorial, best-practices

-- Programming posts (posts 11-15) - Leanne Graham
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (11, 2),
    (11, 3),
    (11, 4), -- programming, java, spring-boot
    (12, 2),
    (12, 5),
    (12, 6), -- programming, jpa, database
    (13, 2),
    (13, 7),
    (13, 8), -- programming, web-development, tutorial
    (14, 2),
    (14, 9),
    (14, 10), -- programming, best-practices, architecture
    (15, 2),
    (15, 11),
    (15, 12);
-- programming, microservices, testing

-- Web development posts (posts 16-20) - Ervin Howell
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (16, 7),
    (16, 14),
    (16, 15), -- web-development, api, security
    (17, 7),
    (17, 6),
    (17, 5), -- web-development, database, jpa
    (18, 7),
    (18, 2),
    (18, 3), -- web-development, programming, java
    (19, 7),
    (19, 4),
    (19, 8), -- web-development, spring-boot, tutorial
    (20, 7),
    (20, 9),
    (20, 10);
-- web-development, best-practices, architecture

-- Database posts (posts 21-25) - Ervin Howell
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (21, 6),
    (21, 5),
    (21, 9), -- database, jpa, best-practices
    (22, 6),
    (22, 7),
    (22, 12), -- database, web-development, testing
    (23, 6),
    (23, 10),
    (23, 13), -- database, architecture, devops
    (24, 6),
    (24, 14),
    (24, 15), -- database, api, security
    (25, 6),
    (25, 8),
    (25, 9);
-- database, tutorial, best-practices

-- Tutorial posts (posts 26-30) - Clementine Bauch
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (26, 8),
    (26, 2),
    (26, 3), -- tutorial, programming, java
    (27, 8),
    (27, 4),
    (27, 5), -- tutorial, spring-boot, jpa
    (28, 8),
    (28, 6),
    (28, 7), -- tutorial, database, web-development
    (29, 8),
    (29, 9),
    (29, 10), -- tutorial, best-practices, architecture
    (30, 8),
    (30, 11),
    (30, 12);
-- tutorial, microservices, testing

-- Best practices posts (posts 31-35) - Clementine Bauch
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (31, 9),
    (31, 1),
    (31, 2), -- best-practices, technology, programming
    (32, 9),
    (32, 3),
    (32, 4), -- best-practices, java, spring-boot
    (33, 9),
    (33, 5),
    (33, 6), -- best-practices, jpa, database
    (34, 9),
    (34, 7),
    (34, 8), -- best-practices, web-development, tutorial
    (35, 9),
    (35, 10),
    (35, 11);
-- best-practices, architecture, microservices

-- Architecture posts (posts 36-40) - Patricia Lebsack
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (36, 10),
    (36, 1),
    (36, 2), -- architecture, technology, programming
    (37, 10),
    (37, 3),
    (37, 4), -- architecture, java, spring-boot
    (38, 10),
    (38, 5),
    (38, 6), -- architecture, jpa, database
    (39, 10),
    (39, 7),
    (39, 8), -- architecture, web-development, tutorial
    (40, 10),
    (40, 9),
    (40, 11);
-- architecture, best-practices, microservices

-- DevOps posts (posts 41-45) - Patricia Lebsack
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (41, 13),
    (41, 12),
    (41, 15), -- devops, testing, security
    (42, 13),
    (42, 11),
    (42, 14), -- devops, microservices, api
    (43, 13),
    (43, 6),
    (43, 7), -- devops, database, web-development
    (44, 13),
    (44, 8),
    (44, 9), -- devops, tutorial, best-practices
    (45, 13),
    (45, 10),
    (45, 1);
-- devops, architecture, technology

-- Security posts (posts 46-50) - Chelsey Dietrich
INSERT INTO
    post_tag (post_id, tag_id)
VALUES (46, 15),
    (46, 14),
    (46, 9), -- security, api, best-practices
    (47, 15),
    (47, 7),
    (47, 6), -- security, web-development, database
    (48, 15),
    (48, 2),
    (48, 3), -- security, programming, java
    (49, 15),
    (49, 4),
    (49, 5), -- security, spring-boot, jpa
    (50, 15),
    (50, 10),
    (50, 11);
-- security, architecture, microservices