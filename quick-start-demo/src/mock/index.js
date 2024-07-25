import Mock from 'mockjs';

// 定义一个随机生成的用户列表
const Random = Mock.Random;
let users = [];

// 初始化用户数据
const generateUsers = () => {
    users = [];
    for (let i = 0; i < 50; i++) {
        let user = {
            id: i + 1,
            idNumber: Random.id(),
            name: Random.cname(),
            birthday: Random.date('yyyy-MM-dd'),
            version: 1,
            selected: false
        };
        users.push(user);
    }
    return users;
};

// 初始化数据
generateUsers();

// 使用 Mock.js 生成假数据
Mock.setup({
    timeout: '200-600'
});

Mock.mock(/\/user/, 'get', (options) => {
    const url = new URL(options.url, window.location.href);
    if (options.url.includes('/user/')) {
        const id = parseInt(options.url.match(/\/user\/(\d+)/)[1], 10);
        const user = users.find(user => user.id === id);
        if (user) {
            return {
                body: user,
                head: {
                    message: '查询成功',
                    code: 200
                }
            };
        } else {
            return {
                head: {
                    message: '用户不存在',
                    code: 404
                }
            };
        }
    }

    const searchParams = url.searchParams;
    const searchQueryName = searchParams.get('name') || '';
    const searchQueryId = searchParams.get('citizenId') || '';

    const filteredUsers = users.filter(user =>
        user.name.includes(searchQueryName) && user.idNumber.includes(searchQueryId)
    );

    return {
        body: {
            data: filteredUsers,
            pageSize: filteredUsers.length,
            pageIndex: 1,
            total: filteredUsers.length
        },
        head: {
            message: '查询成功',
            code: 200
        }
    };
});

Mock.mock('/user', 'post', (options) => {
    const user = JSON.parse(options.body);
    user.id = users.length + 1;
    user.version = 1;
    users.push(user);
    return {
        message: '用户添加成功',
        code: 200
    };
});

Mock.mock('/user', 'put', (options) => {
    const updatedUser = JSON.parse(options.body);
    const index = users.findIndex(user => user.id === updatedUser.id);
    if (index !== -1) {
        users[index] = { ...users[index], ...updatedUser, version: users[index].version + 1 };
        return {
            message: '用户更新成功',
            code: 200
        };
    } else {
        return {
            message: '用户不存在',
            code: 404
        };
    }
});

Mock.mock(/\/optimism\/user/, 'delete', (options) => {
    const url = new URL(options.url, window.location.href);
    const id = parseInt(url.searchParams.get('id'), 10);
    const version = parseInt(url.searchParams.get('version'), 10);

    const index = users.findIndex(user => user.id === id && user.version === version);
    if (index !== -1) {
        users.splice(index, 1);
        return {
            message: '用户删除成功',
            code: 200
        };
    } else {
        return {
            message: '用户不存在或版本不匹配',
            code: 404
        };
    }
});

Mock.mock('/batch/user', 'delete', (options) => {
    const idVersions = JSON.parse(options.body);
    idVersions.forEach(({ id, version }) => {
        const index = users.findIndex(user => user.id === id && user.version === version);
        if (index !== -1) {
            users.splice(index, 1);
        }
    });
    return {
        message: '批量用户删除成功',
        code: 200
    };
});

export default Mock;
