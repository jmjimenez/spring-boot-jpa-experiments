-- Repeatable migration for test profile
-- This migration will be executed every time the application starts with the test profile
-- The R__ prefix makes it a repeatable migration

-- Clear existing data to ensure fresh state
TRUNCATE TABLE Post CASCADE;

TRUNCATE TABLE Users CASCADE;

-- Reset the sequence to start from 1
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE post_id_seq RESTART WITH 1;

-- Insert Users
INSERT INTO
    Users (name, email, username)
VALUES (
        'Leanne Graham',
        'leanne.graham@example.com',
        'leanne_graham'
    ),
    (
        'Ervin Howell',
        'ervin.howell@example.com',
        'ervin_howell'
    ),
    (
        'Clementine Bauch',
        'clementine.bauch@example.com',
        'clementine_bauch'
    ),
    (
        'Patricia Lebsack',
        'patricia.lebsack@example.com',
        'patricia_lebsack'
    ),
    (
        'Chelsey Dietrich',
        'chelsey.dietrich@example.com',
        'chelsey_dietrich'
    );

-- Insert Posts for Leanne Graham (user_id = 1)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440001',
        1,
        'sunt aut facere repellat provident occaecati excepturi optio reprehenderit',
        'quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440002',
        1,
        'qui est esse',
        'est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440003',
        1,
        'ea molestias quasi exercitationem repellat qui ipsa sit aut',
        'et iusto sed quo iure\nvoluptatem occaecati omnis eligendi aut ad\nvoluptatem doloribus vel accusantium quis pariatur\nmolestiae porro eius odio et labore et velit aut'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440004',
        1,
        'eum et est occaecati',
        'ullam et saepe reiciendis voluptatem adipisci\nsit amet autem assumenda provident rerum culpa\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\nquis sunt voluptatem rerum illo velit'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440005',
        1,
        'nesciunt quas odio',
        'repudiandae veniam quaerat sunt sed\nalias aut fugiat sit autem sed est\nvoluptatem omnis possimus esse voluptatibus quis\nest aut tenetur dolor neque'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440006',
        1,
        'dolorem eum magni eos aperiam quia',
        'ut aspernatur corporis harum nihil quis provident sequi\nmollitia nobis aliquid molestiae\nperspiciatis et ea nemo ab reprehenderit accusantium quas\nvoluptate dolores velit et doloremque molestiae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440007',
        1,
        'magnam facilis autem',
        'dolore placeat quibusdam ea quo vitae\nmagni quis enim qui quis quo nemo aut saepe\nquidem repellat excepturi ut quia\nsunt ut sequi eos ea sed quas'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440008',
        1,
        'dolorem dolore est ipsam',
        'dignissimos aperiam dolorem qui eum\nfacilis quibusdam animi sint suscipit qui sint possimus cum\nquaerat magni maiores excepturi\nipsam ut commodi dolor voluptatum modi aut vitae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440009',
        1,
        'nesciunt iure omnis dolorem tempora et accusantium',
        'consectetur animi nesciunt iure dolore\nenim quia ad\nveniam autem ut quam aut nobis\net est aut quod aut provident voluptas autem voluptas'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440010',
        1,
        'optio molestias id quia eum',
        'quo et expedita modi cum officia vel magni\ndoloribus qui repudiandae\nvero nisi sit\nquos veniam quod sed accusamus veritatis error'
    );

-- Insert Posts for Ervin Howell (user_id = 2)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440011',
        2,
        'et ea vero quia laudantium autem',
        'delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\naccusamus in eum beatae sit\nvel qui neque voluptates ut commodi qui incidunt\nut animi commodi'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440012',
        2,
        'in quibusdam tempore odit est dolorem',
        'itaque id aut magnam\npraesentium quia et ea odit et ea voluptas et\nsapiente quia nihil amet occaecati quia id voluptatem\nincidunt ea est distinctio odio'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440013',
        2,
        'dolorum ut in voluptas mollitia et saepe quo animi',
        'aut dicta possimus sint mollitia voluptas commodi quo doloremque\niste corrupti reiciendis voluptatem eius rerum\nsit cumque quod eligendi laborum minima\nperferendis recusandae assumenda consectetur porro architecto ipsum ipsam'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440014',
        2,
        'voluptatem eligendi optio',
        'fuga et accusamus dolorum perferendis illo voluptas\nnon doloremque neque facere\nad qui dolorum molestiae beatae\nsed aut voluptas totam sit illum'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440015',
        2,
        'eveniet quod temporibus',
        'reprehenderit quos placeat\nvelit minima officia dolores impedit repudiandae molestiae nam\nvoluptas recusandae quis delectus\nofficiis harum fugiat vitae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440016',
        2,
        'sint suscipit perspiciatis velit dolorum rerum ipsa laboriosam odio',
        'suscipit nam nisi quo aperiam aut\nasperiores eos fugit maiores voluptatibus quia\nvoluptatem quis ullam qui in alias quia est\nconsequatur magni mollitia accusamus ea nisi voluptate dicta'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440017',
        2,
        'fugit voluptas sed molestias voluptatem provident',
        'eos voluptas et aut odit natus earum\naspernatur fuga molestiae ullam\ndeserunt ratione qui eos\nqui nihil ratione nemo velit ut aut id quo'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440018',
        2,
        'voluptate et itaque vero tempora molestiae',
        'eveniet quo quis\nlaborum totam consequatur non dolor\nut et est repudiandae\nest voluptatem vel debitis et magnam'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440019',
        2,
        'adipisci placeat illum aut reiciendis qui',
        'illum quis cupiditate provident sit magnam\nea sed aut omnis\nveniam maiores ullam consequatur atque\nadipisci quo iste expedita sit quos voluptas'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440020',
        2,
        'doloribus ad provident suscipit at',
        'qui consequuntur ducimus possimus quisquam amet similique\nsuscipit porro ipsam amet\neos veritatis officiis exercitationem vel fugit aut necessitatibus totam\nomnis rerum consequatur expedita quidem cumque explicabo'
    );

-- Insert Posts for Clementine Bauch (user_id = 3)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440021',
        3,
        'asperiores ea ipsam voluptatibus modi minima quia sint',
        'repellat aliquid praesentium dolorem quo\nsed totam minus non itaque\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\ntempora et tenetur expedita sunt'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440022',
        3,
        'dolor sint quo a velit explicabo quia nam',
        'eos qui et ipsum ipsam suscipit aut\nsed omnis non odio\nexpedita earum mollitia molestiae aut atque rem suscipit\nnam impedit esse'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440023',
        3,
        'maxime id vitae nihil numquam',
        'veritatis unde neque eligendi\nquae quod architecto quo neque vitae\nest illo sit tempora doloremque fugit quod\net et vel beatae sequi ullam sed tenetur perspiciatis'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440024',
        3,
        'autem hic labore sunt dolores incidunt',
        'enim et ex nulla\nomnis voluptas quia qui\nvoluptatem consequatur numquam aliquam sunt\ntotam recusandae id dignissimos aut sed asperiores deserunt'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440025',
        3,
        'rem alias distinctio quo quis',
        'ullam consequatur ut\nomnis quis sit vel consequuntur\nipsa eligendi ipsum molestiae et omnis error nostrum\nmolestiae illo tempore quia et distinctio'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440026',
        3,
        'est et quae odit qui non',
        'similique esse doloribus nihil accusamus\nomnis dolorem fuga consequuntur reprehenderit fugit recusandae temporibus\nperspiciatis cum ut laudantium\nomnis aut molestiae vel vero'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440027',
        3,
        'quasi id et eos tenetur aut quo autem',
        'eum sed dolores ipsam sint possimus debitis occaecati\ndebitis qui qui et\nut placeat enim earum aut odit facilis\nconsequatur suscipit necessitatibus rerum sed inventore temporibus consequatur'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440028',
        3,
        'delectus ullam et corporis nulla voluptas sequi',
        'non et quaerat ex quae ad maiores\nmaiores recusandae totam aut blanditiis mollitia quas illo\nut voluptatibus voluptatem\nsimilique nostrum eum'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440029',
        3,
        'iusto eius quod necessitatibus culpa ea',
        'odit magnam ut saepe sed non qui\ntempora atque nihil\naccusamus illum doloribus illo dolor\neligendi repudiandae odit magni similique sed cum maiores'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440030',
        3,
        'a quo magni similique perferendis',
        'alias dolor cumque\nimpedit blanditiis non eveniet odio maxime\nblanditiis amet eius quis tempora quia autem rem\na provident perspiciatis quia'
    );

-- Insert Posts for Patricia Lebsack (user_id = 4)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440031',
        4,
        'ullam ut quidem id aut vel consequuntur',
        'debitis eius sed quibusdam non quis consectetur vitae\nimpedit ut qui consequatur sed aut in\nquidem sit nostrum et maiores adipisci atque\nquaerat voluptatem adipisci repudiandae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440032',
        4,
        'doloremque illum aliquid sunt',
        'deserunt eos nobis asperiores et hic\nest debitis repellat molestiae optio\nnihil ratione ut eos beatae quibusdam distinctio maiores\nearum voluptates et aut adipisci ea maiores voluptas maxime'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440033',
        4,
        'qui explicabo molestiae dolorem',
        'rerum ut et numquam laborum odit est sit\nid qui sint in\nquasi tenetur tempore aperiam et quaerat qui in\nrerum officiis sequi cumque quod'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440034',
        4,
        'magnam ut rerum iure',
        'ea velit perferendis earum ut voluptatem voluptate itaque iusto\ntotam pariatur in\nnemo voluptatem voluptatem autem magni tempora minima in\nest distinctio qui assumenda accusamus dignissimos officia nesciunt nobis'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440035',
        4,
        'id nihil consequatur molestias animi provident',
        'nisi error delectus possimus ut eligendi vitae\nplaceat eos harum cupiditate facilis reprehenderit voluptatem beatae\nmodi ducimus quo illum voluptas eligendi\net nobis quia fugit'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440036',
        4,
        'fuga nam accusamus voluptas reiciendis itaque',
        'ad mollitia et omnis minus architecto odit\nvoluptas doloremque maxime aut non ipsa qui alias veniam\nblanditiis culpa aut quia nihil cumque facere et occaecati\nqui aspernatur quia eaque ut aperiam inventore'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440037',
        4,
        'provident vel ut sit ratione est',
        'debitis et eaque non officia sed nesciunt pariatur vel\nvoluptatem iste vero et ea\nnumquam aut expedita ipsum nulla in\nvoluptates omnis consequatur aut enim officiis in quam qui'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440038',
        4,
        'explicabo et eos deleniti nostrum ab id repellendus',
        'animi esse sit aut sit nesciunt assumenda eum voluptas\nquia voluptatibus provident quia necessitatibus ea\nrerum repudiandae quia voluptatem delectus fugit aut id quia\nratione optio eos iusto veniam iure'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440039',
        4,
        'eos dolorem iste accusantium est eaque quam',
        'corporis rerum ducimus vel eum accusantium\nmaxime aspernatur a porro possimus iste omnis\nest in deleniti asperiores fuga aut\nvoluptas sapiente vel dolore minus voluptatem incidunt ex'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440040',
        4,
        'enim quo cumque',
        'ut voluptatum aliquid illo tenetur nemo sequi quo facilis\nipsum rem optio mollitia quas\nvoluptatem eum voluptas qui\nunde omnis voluptatem iure quasi maxime voluptas nam'
    );

-- Insert Posts for Chelsey Dietrich (user_id = 5)
INSERT INTO
    Post (uuid, user_id, title, body)
VALUES (
        '550e8400-e29b-41d4-a716-446655440041',
        5,
        'non est facere',
        'molestias id nostrum\nexcepturi molestiae dolore omnis repellendus quaerat saepe\nconsectetur iste quaerat tenetur asperiores accusamus ex ut\nnam quidem est ducimus sunt debitis saepe'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440042',
        5,
        'commodi ullam sint et excepturi error explicabo praesentium voluptas',
        'odio fugit voluptatum ducimus earum autem est incidunt voluptatem\nodit reiciendis aliquam sunt sequi nulla dolorem\nnon facere repellendus voluptates quia\nratione harum vitae ut'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440043',
        5,
        'eligendi iste nostrum consequuntur adipisci praesentium sit beatae perferendis',
        'similique fugit est\nillum et dolorum harum et voluptate eaque quidem\nexercitationem quos nam commodi possimus cum odio nihil nulla\ndolorum exercitationem magnam ex et a et distinctio debitis'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440044',
        5,
        'optio dolor molestias sit',
        'temporibus est consectetur dolore\net libero debitis vel velit laboriosam quia\nipsum quibusdam qui itaque fuga rem aut\nea et iure quam sed maxime ut distinctio quae'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440045',
        5,
        'ut numquam possimus omnis eius suscipit laudantium iure',
        'est natus reiciendis nihil possimus aut provident\nex et dolor\nrepellat pariatur est\nnobis rerum repellendus dolorem autem'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440046',
        5,
        'aut quo modi neque nostrum ducimus',
        'voluptatem quisquam iste\nvoluptatibus natus officiis facilis dolorem\nquis quas ipsam\nvel et voluptatum in aliquid'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440047',
        5,
        'quibusdam cumque rem aut deserunt',
        'voluptatem assumenda ut qui ut cupiditate aut impedit veniam\noccaecati nemo illum voluptatem laudantium\nmolestiae beatae rerum ea iure soluta nostrum\neligendi et voluptate'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440048',
        5,
        'ut voluptatem illum ea doloribus itaque eos',
        'voluptates quo voluptatem facilis iure occaecati\nvel assumenda rerum officia et\nillum perspiciatis ab deleniti\nlaudantium repellat ad ut et autem reprehenderit'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440049',
        5,
        'laborum non sunt aut ut assumenda perspiciatis voluptas',
        'inventore ab sint\nnatus fugit id nulla sequi architecto nihil quaerat\neos tenetur in in eum veritatis non\nquibusdam officiis aspernatur cumque aut commodi aut'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440050',
        5,
        'repellendus qui recusandae incidunt voluptates tenetur qui omnis exercitationem',
        'error suscipit maxime adipisci consequuntur recusandae\nvoluptas eligendi et est et voluptates\nquia distinctio ab amet quaerat molestiae et vitae\nadipisci impedit sequi nesciunt quis consectetur'
    );